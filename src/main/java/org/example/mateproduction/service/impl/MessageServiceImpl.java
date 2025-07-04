package org.example.mateproduction.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.entity.Chat;
import org.example.mateproduction.entity.Message;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.ChatRepository;
import org.example.mateproduction.repository.MessageRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.MessageService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @Override
    public MessageResponse saveAndSendMessage(MessageRequest request, Principal principal) throws NotFoundException {
        User sender = findUserByPrincipal(principal);
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Receiver not found with ID: " + request.getReceiverId()));

        // Step 1: Find the chat OR create and save a new one immediately.
        // This makes sure the Chat record exists in the database before proceeding.
        Chat chat = chatRepository.findChatByParticipants(sender, receiver)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .participant1(sender)
                            .participant2(receiver)
                            .build();
                    return chatRepository.save(newChat);
                });

        // Step 2: Now that the Chat is guaranteed to exist, create the Message.
        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .isRead(false)
                .build();

        // Step 3: Explicitly save the message. This returns the fully persisted object.
        Message savedMessage = messageRepository.save(message);

        // Step 4: Prepare the response and broadcast it.
        MessageResponse response = mapToResponse(savedMessage, receiver.getId());

        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/messages", response);
        messagingTemplate.convertAndSendToUser(receiver.getEmail(), "/queue/messages", response);

        return response;
    }


    @Override
    public List<MessageResponse> getChatHistory(UUID companionId, Principal principal) throws NotFoundException {
        User currentUser = findUserByPrincipal(principal);
        User companion = userRepository.findById(companionId)
                .orElseThrow(() -> new NotFoundException("Companion user not found with ID: " + companionId));

        // Find the chat without throwing an exception if it's not found
        Optional<Chat> chatOptional = chatRepository.findChatByParticipants(currentUser, companion);

        // If no chat exists between the users yet, it's a new conversation.
        // Return an empty list instead of throwing a 404 error.
        if (chatOptional.isEmpty()) {
            return Collections.emptyList();
        }

        // If the chat exists, proceed to get its messages.
        Chat chat = chatOptional.get();
        return messageRepository.findByChatOrderByCreatedAtAsc(chat).stream()
                .map(message -> mapToResponse(message, companionId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatPreviewResponse> getUserChats(Principal principal) throws NotFoundException {
        User currentUser = findUserByPrincipal(principal);
        List<Chat> chats = chatRepository.findAllByUser(currentUser);

        return chats.stream().map(chat -> {
                    User companion = chat.getParticipant1().getId().equals(currentUser.getId()) ? chat.getParticipant2() : chat.getParticipant1();

                    Message lastMessage = chat.getMessages().stream()
                            // 1. Filter out any message that might have a null timestamp.
                            .filter(m -> m.getCreatedAt() != null)
                            // 2. Now, safely find the max. This will correctly result in orElse(null)
                            //    if the messages list is empty or contains only null-timestamp messages.
                            .max(Comparator.comparing(Message::getCreatedAt))
                            .orElse(null);
                    // --- MODIFICATION END ---


                    long unreadCount = chat.getMessages().stream()
                            .filter(msg -> !msg.getSender().getId().equals(currentUser.getId()) && !Boolean.TRUE.equals(msg.getIsRead()))
                            .count();

                    return ChatPreviewResponse.builder()
                            .chatId(chat.getId())
                            .companionId(companion.getId())
                            .companionName(companion.getName() + " " + companion.getSurname())
                            .companionAvatarUrl(companion.getAvatarUrl())
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : "No messages yet.")
                            .lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null)
                            .hasUnreadMessages(unreadCount > 0)
                            .build();
                }) // Sort the final list of chat previews to show the most recent conversations first.
                .sorted(Comparator.comparing(ChatPreviewResponse::getLastMessageTime,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markMessagesAsRead(UUID companionId, Principal principal) throws NotFoundException {
        User currentUser = findUserByPrincipal(principal);
        User companion = userRepository.findById(companionId)
                .orElseThrow(() -> new NotFoundException("Companion user not found with ID: " + companionId));

        chatRepository.findChatByParticipants(currentUser, companion)
                .ifPresent(chat -> {
                    List<Message> unreadMessages = chat.getMessages().stream()
                            .filter(msg -> msg.getSender().getId().equals(companionId) && !Boolean.TRUE.equals(msg.getIsRead()))
                            .toList();

                    if (unreadMessages.isEmpty()) {
                        return; // Nothing to do
                    }

                    for (Message message : unreadMessages) {
                        message.setIsRead(true);
                    }

                    messageRepository.saveAll(unreadMessages);

                    // Convert to DTOs for broadcasting
                    List<MessageResponse> updatedMessageDTOs = unreadMessages.stream()
                            .map(m -> mapToResponse(m, currentUser.getId()))
                            .collect(Collectors.toList());

                    // Broadcast the update to both users
                    messagingTemplate.convertAndSendToUser(currentUser.getEmail(), "/queue/messages-read", updatedMessageDTOs);
                    messagingTemplate.convertAndSendToUser(companion.getEmail(), "/queue/messages-read", updatedMessageDTOs);
                });
    }

    private User findUserByPrincipal(Principal principal) throws NotFoundException {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new NotFoundException("Authenticated user not found."));
    }

    private MessageResponse mapToResponse(Message message, UUID receiverId) {
        return MessageResponse.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .receiverId(receiverId)
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.getIsRead())
                .build();
    }
}