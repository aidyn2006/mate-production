package org.example.mateproduction.service.impl;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;

    @Override
    @Transactional
    public MessageResponse sendMessage(MessageRequest request, String email) throws NotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
//        UUID senderId =getCurrentUserId();
        UUID receiverId = request.getReceiverId();

        User sender = userRepository.findById(user.getId()).orElseThrow(() -> new NotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new NotFoundException("Receiver not found"));

        Chat chat = chatRepository.findBySenderAndReceiver(sender, receiver).or(() -> chatRepository.findBySenderAndReceiver(receiver, sender)).orElseGet(() -> {
            Chat newChat = Chat.builder().sender(sender).receiver(receiver).build();
            return chatRepository.save(newChat);
        });

        Message message = Message.builder().chat(chat).sender(sender).content(request.getContent()).createdAt(new Date()).isRead(false).build();

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Override
    public List<MessageResponse> getChatHistory(UUID senderId, UUID receiverId) throws NotFoundException {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new NotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new NotFoundException("Receiver not found"));

        List<Message> messages = messageRepository.findChatMessages(sender.getId(), receiver.getId());

        return messages.stream().sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())).map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public List<ChatPreviewResponse> getUserChats(UUID currentUserId) throws NotFoundException {
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new NotFoundException("User not found"));

        List<Chat> chats = chatRepository.findAllBySenderOrReceiver(currentUser, currentUser);

        return chats.stream().map(chat -> {
            User companion = chat.getSender().getId().equals(currentUserId) ? chat.getReceiver() : chat.getSender();

            Message lastMessage = chat.getMessages().stream().max((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt())).orElse(null);

            boolean hasUnread = chat.getMessages().stream().anyMatch(msg -> !msg.getSender().getId().equals(currentUserId) && !Boolean.TRUE.equals(msg.getIsRead()));

            return ChatPreviewResponse.builder().chatId(chat.getId()).companionId(companion.getId()).companionName(companion.getName() + " " + companion.getSurname()).companionAvatarUrl(companion.getAvatarUrl()).lastMessage(lastMessage != null ? lastMessage.getContent() : "").lastMessageTime(lastMessage != null ? lastMessage.getCreatedAt() : null).hasUnreadMessages(hasUnread).build();
        }).sorted((c1, c2) -> {
            Date time1 = c1.getLastMessageTime() != null ? c1.getLastMessageTime() : new Date(0);
            Date time2 = c2.getLastMessageTime() != null ? c2.getLastMessageTime() : new Date(0);
            return time2.compareTo(time1); // сортировка по убыванию
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markMessagesAsRead(UUID senderId, UUID receiverId) {
        List<Message> unreadMessages = messageRepository.findChatMessages(senderId, receiverId).stream().filter(msg -> {
            UUID actualReceiverId = msg.getChat().getReceiver().getId().equals(msg.getSender().getId()) ? msg.getChat().getSender().getId() : msg.getChat().getReceiver().getId();
            return actualReceiverId.equals(receiverId) && !Boolean.TRUE.equals(msg.getIsRead());
        }).collect(Collectors.toList());

        for (Message message : unreadMessages) {
            message.setIsRead(true);
        }

        messageRepository.saveAll(unreadMessages);
    }


    @Override
    public void deleteMessage(UUID messageId) throws NotFoundException {
        if (!messageRepository.existsById(messageId)) {
            throw new NotFoundException("Message not found");
        }
        messageRepository.deleteById(messageId);
    }

    @Override
    public void clearChat(UUID senderId, UUID receiverId) {
        List<Message> messagesToDelete = messageRepository.findChatMessages(senderId, receiverId);
        messageRepository.deleteAll(messagesToDelete);
    }

    private MessageResponse mapToResponse(Message message) {
        return MessageResponse.builder().id(message.getId()).chatId(message.getChat().getId()).senderId(message.getSender().getId()).receiverId(message.getChat().getReceiver().getId().equals(message.getSender().getId()) ? message.getChat().getSender().getId() : message.getChat().getReceiver().getId()).content(message.getContent()).createdAt(message.getCreatedAt()).isRead(message.getIsRead()).build();
    }

}
