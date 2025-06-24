package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @MessageMapping("/chat")
    public void handleMessage(MessageRequest messageRequest, SimpMessageHeaderAccessor headerAccessor) throws NotFoundException {

        String authenticatedEmail = null;

        // 1. Попробуем получить токен из WebSocket-заголовка
        String token = headerAccessor.getFirstNativeHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            try {
                String email = jwtService.extractUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtService.isTokenValid(token, userDetails)) {
                    authenticatedEmail = email;
                    System.out.println("Chat message authenticated as: " + authenticatedEmail);
                }
            } catch (Exception e) {
                System.out.println("Ошибка валидации токена: " + e.getMessage());
            }
        }

        if (authenticatedEmail == null) {
            System.out.println("Неавторизованный WebSocket-запрос в /chat");
            throw new SecurityException("Пользователь не аутентифицирован");
        }

        // 2. Отправка сообщения
        MessageResponse savedMessage = messageService.sendMessage(messageRequest, authenticatedEmail);

        // 3. Отправка сообщения отправителю и получателю
        messagingTemplate.convertAndSendToUser(savedMessage.getSenderId().toString(), "/queue/messages", savedMessage);

        messagingTemplate.convertAndSendToUser(savedMessage.getReceiverId().toString(), "/queue/messages", savedMessage);
    }
}
