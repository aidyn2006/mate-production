// src/main/java/org/example/mateproduction/controller/WebSocketMessageController.java
package org.example.mateproduction.controller;

import lombok.RequiredArgsConstructor;
import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.dto.request.MarkReadRequest;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.exception.NotFoundException;
import org.example.mateproduction.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // Импорт
import java.security.Principal; // Импорт
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Date;
import java.util.UUID;

@Controller
public class WebSocketMessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final JwtService jwtService; // Добавляем для валидации токена
    private final UserDetailsService userService; // Добавляем для получения UserDetails

    @Autowired
    public WebSocketMessageController(MessageService messageService,
                                      SimpMessagingTemplate messagingTemplate,
                                      JwtService jwtService,
                                      UserDetailsService userService) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@RequestBody MessageRequest messageRequest,
                            SimpMessageHeaderAccessor headerAccessor) throws NotFoundException {

        // Способ 1: Используем Principal если он установлен
        Principal principal = headerAccessor.getUser();
        String authenticatedEmail = null;

        if (principal != null) {
            authenticatedEmail = principal.getName();
            System.out.println("Message received from authenticated user: " + authenticatedEmail);
        } else {
            // Способ 2: Получаем токен из заголовков сообщения
            String token = headerAccessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    String email = jwtService.extractUsername(token);
                    UserDetails userDetails = userService.loadUserByUsername(email);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        authenticatedEmail = email;
                        System.out.println("Message received with token authentication: " + authenticatedEmail);
                    }
                } catch (Exception e) {
                    System.out.println("Token validation failed: " + e.getMessage());
                }
            }
        }

        // Способ 3: Если нет аутентификации, используем senderId из запроса (менее безопасно)
        if (authenticatedEmail == null) {
            System.out.println("WARNING: No authentication found, using senderId from request");
            // В этом случае вы должны дополнительно валидировать senderId
        }

        // Сохраняем сообщение
        MessageResponse savedMessage = messageService.sendMessage(messageRequest,authenticatedEmail);

        // Отправляем сообщение получателям
        messagingTemplate.convertAndSendToUser(
                savedMessage.getSenderId().toString(),
                "/queue/messages",
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/messages",
                savedMessage
        );
    }

    @MessageMapping("/chat.markRead")
    public void markRead(@RequestBody MarkReadRequest request,
                         SimpMessageHeaderAccessor headerAccessor) {

        Principal principal = headerAccessor.getUser();
        if (principal == null) {
            // Попытка получить токен из заголовков
            String token = headerAccessor.getFirstNativeHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                try {
                    String email = jwtService.extractUsername(token);
                    UserDetails userDetails = userService.loadUserByUsername(email);
                    if (jwtService.isTokenValid(token, userDetails)) {
                        System.out.println("MarkRead from token authenticated user: " + email);
                        // Продолжаем обработку
                    } else {
                        throw new SecurityException("Invalid authentication token");
                    }
                } catch (Exception e) {
                    throw new SecurityException("Authentication failed: " + e.getMessage());
                }
            } else {
                throw new SecurityException("User not authenticated for WebSocket markRead");
            }
        } else {
            System.out.println("MarkRead from principal authenticated user: " + principal.getName());
        }

        // Обработка запроса на отметку как прочитанное
//        messageService.markAsRead(request);

        // Отправляем уведомление о прочтении
        messagingTemplate.convertAndSendToUser(
                request.getSenderId().toString(),
                "/queue/readReceipts",
                request
        );
    }
}