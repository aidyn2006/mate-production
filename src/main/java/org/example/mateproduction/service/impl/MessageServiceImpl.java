package org.example.mateproduction.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.Message;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.repository.MessageRepository;
import org.example.mateproduction.repository.UserRepository;
import org.example.mateproduction.service.MessageService;
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

    @Override
    @Transactional
    public MessageResponse sendMessage(MessageRequest request) throws NotFoundException {
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new NotFoundException("Sender not found"));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new NotFoundException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(request.getContent());
        message.setCreatedAt(new Date());

        Message saved = messageRepository.save(message);
        return mapToResponse(saved);
    }

    @Override
    public List<MessageResponse> getChatHistory(UUID senderId, UUID receiverId) {
        List<Message> messages = messageRepository.findChatMessages(senderId, receiverId);
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
                .senderId(mapUser(message.getSender()))
                .receiverId(mapUser(message.getReceiver()))
                .content(message.getContent())
                .createdAt(message.getCreatedAt())
                .build();
    }

    private UserResponse mapUser(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getName()+" "+user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
