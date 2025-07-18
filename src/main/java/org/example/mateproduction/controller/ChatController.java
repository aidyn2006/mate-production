package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MarkReadRequest;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final MessageService messageService;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload @Validated MessageRequest request, Principal principal) throws NotFoundException {
        // UPGRADE: Use @AuthenticationPrincipal for cleaner, more secure access to the user
        // The service now takes the User object directly, improving decoupling.
        messageService.saveAndSendMessage(request, principal);
    }

    @MessageMapping("/chat.markRead")
    public void markAsRead(@Payload MarkReadRequest request, Principal principal) throws NotFoundException {
        // UPGRADE: Pass the full User object to the service
        messageService.markMessagesAsRead(request.getSenderId(), principal);
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