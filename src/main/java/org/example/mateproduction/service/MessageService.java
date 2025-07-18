package org.example.mateproduction.service;

import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface MessageService {
    void saveAndSendMessage(MessageRequest request, Principal principal) throws NotFoundException;

    List<MessageResponse> getChatHistory(UUID companionId, Principal principal) throws NotFoundException;

    List<ChatPreviewResponse> getUserChats(Principal principal) throws NotFoundException;

    void markMessagesAsRead(UUID companionId, Principal principal) throws NotFoundException;

}