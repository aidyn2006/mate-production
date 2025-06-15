package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MessageRequest;
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
    public MessageResponse sendMessage(MessageRequest request) throws NotFoundException {
        UUID senderId =getCurrentUserId();
        UUID receiverId = request.getReceiverId();

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("Receiver not found"));

        // Получаем или создаём чат
        Chat chat = chatRepository
                .findBySenderAndReceiver(sender, receiver)
                .or(() -> chatRepository.findBySenderAndReceiver(receiver, sender))
                .orElseGet(() -> {
                    Chat newChat = Chat.builder()
                            .sender(sender)
                            .receiver(receiver)
                            .build();
                    return chatRepository.save(newChat);
                });

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .createdAt(new Date())
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Override
    public List<MessageResponse> getChatHistory(UUID senderId, UUID receiverId) throws NotFoundException {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new NotFoundException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new NotFoundException("Receiver not found"));

        List<Message> messages = messageRepository.findChatMessages(sender.getId(), receiver.getId());

        return messages.stream()
                .sorted((m1, m2) -> m1.getCreatedAt().compareTo(m2.getCreatedAt()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
        return MessageResponse.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .receiverId(
                        message.getChat().getReceiver().getId().equals(message.getSender().getId())
                                ? message.getChat().getSender().getId()
                                : message.getChat().getReceiver().getId()
                )
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .isRead(message.getIsRead())
                .build();
    }


    private UUID getCurrentUserId() {
        return UUID.fromString(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
