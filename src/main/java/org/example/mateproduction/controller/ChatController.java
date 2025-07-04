package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MarkReadRequest;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.dto.response.UserResponse;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.MessageService;
import org.example.mateproduction.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;
    private UserService userService;
    private SimpMessageSendingOperations messagingTemplate;

    // --- WebSocket Endpoints ---

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload @Validated MessageRequest request, Principal principal) throws NotFoundException {
        if (principal == null) {
            throw new SecurityException("User not authenticated for sending messages.");
        }
        messageService.saveAndSendMessage(request, principal);
    }

    @MessageMapping("/chat.markRead")
    public void markAsRead(@Payload MarkReadRequest request, Principal principal) throws NotFoundException {
        if (principal == null) {
            throw new SecurityException("User not authenticated for marking messages as read.");
        }

        // 1. Get the list of updated messages from the service
        List<MessageResponse> updatedMessages = messageService.markMessagesAsRead(request.getSenderId(), principal);

        // 2. If any messages were updated, broadcast them to both users
        if (!updatedMessages.isEmpty()) {
            UserResponse companion = userService.getById(request.getSenderId()); // Assuming you have such a method in a user service

            // Send to the user who marked the messages as read
            messagingTemplate.convertAndSendToUser(principal.getName(), "/queue/messages-read", updatedMessages);
            // Send to the user who sent the messages
            messagingTemplate.convertAndSendToUser(companion.getEmail(), "/queue/messages-read", updatedMessages);
        }
    }

    @GetMapping("/api/v1/messages/chats")
    public ResponseEntity<List<ChatPreviewResponse>> getUserChats(Principal principal) throws NotFoundException {
        return ResponseEntity.ok(messageService.getUserChats(principal));
    }

    @GetMapping("/api/v1/messages/history/{companionId}")
    public ResponseEntity<List<MessageResponse>> getChatHistory(
            @PathVariable UUID companionId,
            Principal principal) throws NotFoundException {
        return ResponseEntity.ok(messageService.getChatHistory(companionId, principal));
    }
}