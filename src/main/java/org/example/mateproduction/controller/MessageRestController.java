package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MarkReadRequest;
import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.MessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageRestController {

    private final MessageService messageService;

    @GetMapping("/chats/{userId}")
    public List<ChatPreviewResponse> getUserChats(@PathVariable UUID userId) throws NotFoundException {
        return messageService.getUserChats(userId);
    }

    @GetMapping("/history")
    public List<MessageResponse> getChatHistory(@RequestParam UUID senderId, @RequestParam UUID receiverId) throws NotFoundException {
        return messageService.getChatHistory(senderId, receiverId);
    }

    @PostMapping("/read")
    public void markAsRead(@RequestBody MarkReadRequest request) {
        messageService.markMessagesAsRead(request.getSenderId(), request.getReceiverId());
    }
//    @PostMapping("/send")
//    public ResponseEntity<MessageResponse> sendMessageRest(@RequestBody MessageRequest request)
//            throws NotFoundException {
//        MessageResponse resp = messageService.sendMessage(request);
//        return ResponseEntity.ok(resp);
//    }

}
