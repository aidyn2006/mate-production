package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public void handleMessage(MessageRequest messageRequest) throws NotFoundException {
        MessageResponse savedMessage = messageService.sendMessage(messageRequest);
        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/messages",
                savedMessage
        );
        messagingTemplate.convertAndSendToUser(
                savedMessage.getSenderId().toString(),
                "/queue/messages",
                savedMessage
        );
    }
}
