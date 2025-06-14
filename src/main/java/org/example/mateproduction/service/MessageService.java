package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.util.List;
import java.util.UUID;

public interface MessageService {
    MessageResponse sendMessage(MessageRequest request) throws NotFoundException;
    List<MessageResponse> getChatHistory(UUID senderId, UUID receiverId) throws NotFoundException;
    void deleteMessage(UUID messageId) throws NotFoundException;
    void clearChat(UUID senderId, UUID receiverId);
}