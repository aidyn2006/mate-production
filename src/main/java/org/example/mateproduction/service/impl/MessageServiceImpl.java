package org.example.mateproduction.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtUserDetails;
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
    @Transactional
    public void saveAndSendMessage(MessageRequest request, Principal principal) throws NotFoundException {
        User sender = findUserByPrincipal(principal);
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Receiver not found with ID: " + request.getReceiverId()));

        // Find or create the chat with consistent participant order
        Chat chat = findOrCreateChat(sender, receiver);

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .isRead(false)
                .build();

        Message savedMessage = messageRepository.save(message);
        MessageResponse response = mapToResponse(savedMessage, receiver.getId());

        // UPGRADE: Send to a chat-specific topic instead of a generic user queue
        String chatTopic = String.format("/topic/chats/%s", chat.getId());
        messagingTemplate.convertAndSend(chatTopic, response);
    }

    private Chat findOrCreateChat(User user1, User user2) {
        return chatRepository.findByParticipants(user1, user2)
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .participant1(user1)
                            .participant2(user2)
                            .build();
                    return chatRepository.save(newChat);
                });
    }


    private Chat createChatBetweenUsers(User sender, User receiver) {
        Chat newChat = Chat.builder()
                .participant1(sender)
                .participant2(receiver)
                .build();
        return chatRepository.save(newChat);
    }



    @Override
    public List<MessageResponse> getChatHistory(UUID companionId, Principal principal) throws NotFoundException {
        User currentUser = findUserByPrincipal(principal);
        User companion = userRepository.findById(companionId)
                .orElseThrow(() -> new NotFoundException("Companion user not found with ID: " + companionId));

        Chat chat = findOrCreateChat(currentUser, companion);

        return messageRepository.findByChatOrderByCreatedAtAsc(chat).stream()
                .map(message -> mapToResponse(message, companionId)) // receiverId is the other person
                .collect(Collectors.toList());
    }

    @Override
    public List<ChatPreviewResponse> getUserChats(Principal principal) throws NotFoundException {
        User currentUser = findUserByPrincipal(principal);
        // UPGRADE: Use the new, highly performant repository method
        return chatRepository.findChatPreviewsByUser(currentUser.getId());
    }

    @Override
    @Transactional
    public void markMessagesAsRead(UUID companionId, Principal principal) throws NotFoundException {
        User currentUser = findUserByPrincipal(principal);
        User companion = userRepository.findById(companionId)
                .orElseThrow(() -> new NotFoundException("Companion user not found with ID: " + companionId));

        Chat chat = findOrCreateChat(currentUser, companion);

        List<Message> unreadMessages = messageRepository.findByChatAndSenderAndIsReadIsFalse(chat, companion);

        if (unreadMessages.isEmpty()) {
            return;
        }

        unreadMessages.forEach(message -> message.setIsRead(true));
        messageRepository.saveAll(unreadMessages);

        List<MessageResponse> updatedMessageDTOs = unreadMessages.stream()
                .map(m -> mapToResponse(m, currentUser.getId()))
                .collect(Collectors.toList());

        // Also broadcast this update to the chat-specific topic for consistency
        String chatTopic = String.format("/topic/chats/%s/read", chat.getId());
        messagingTemplate.convertAndSend(chatTopic, updatedMessageDTOs);
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