package org.example.mateproduction.config;

import org.example.mateproduction.config.Jwt.JwtService;
import org.example.mateproduction.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.security.Principal;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtService jwtService; // Ваш сервис для работы с JWT

    @Autowired
    private UserDetailsService userService; // Сервис для получения пользователей

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setHandshakeHandler(handshakeHandler())
                .setAllowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .withSockJS();
    }

    @Bean
    public DefaultHandshakeHandler handshakeHandler() {
        return new DefaultHandshakeHandler() {
            @Override
            protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
                // Попытка получить токен из параметров запроса
                String token = getTokenFromRequest(request);

                if (token != null) {
                    try {
                        String email = jwtService.extractUsername(token);
                        UserDetails userDetails = userService.loadUserByUsername(email);
                        if (jwtService.isTokenValid(token, userDetails)) {
                            System.out.println("DEBUG: WebSocket Handshake - User authenticated: " + email);
                            return new StompPrincipal(email);
                        }
                    } catch (Exception e) {
                        System.out.println("DEBUG: WebSocket Handshake - Token validation failed: " + e.getMessage());
                    }
                }

                System.out.println("DEBUG: WebSocket Handshake - No valid authentication found");
                return null;
            }

            private String getTokenFromRequest(ServerHttpRequest request) {
                // Проверяем параметры запроса
                String query = request.getURI().getQuery();
                if (query != null && query.contains("token=")) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("token=")) {
                            return param.substring(6); // Убираем "token="
                        }
                    }
                }

                // Проверяем заголовки
                List<String> authHeaders = request.getHeaders().get("Authorization");
                if (authHeaders != null && !authHeaders.isEmpty()) {
                    String authHeader = authHeaders.get(0);
                    if (authHeader.startsWith("Bearer ")) {
                        return authHeader.substring(7);
                    }
                }

                return null;
            }
        };
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Получаем токен из заголовков STOMP CONNECT
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                    }

                    if (token != null) {
                        try {
                            String email = jwtService.extractUsername(token);
                            UserDetails userDetails = userService.loadUserByUsername(email);
                            if (jwtService.isTokenValid(token, userDetails)) {
                                accessor.setUser(new StompPrincipal(email));
                                System.out.println("DEBUG: STOMP CONNECT - User authenticated: " + email);
                            } else {
                                System.out.println("DEBUG: STOMP CONNECT - Invalid token");
                                throw new IllegalArgumentException("Invalid authentication token");
                            }
                        } catch (Exception e) {
                            System.out.println("DEBUG: STOMP CONNECT - Authentication failed: " + e.getMessage());
                            throw new IllegalArgumentException("Invalid authentication token");
                        }
                    } else {
                        System.out.println("DEBUG: STOMP CONNECT - No token provided");
                        throw new IllegalArgumentException("Authentication token required");
                    }
                }

                return message;
            }
        });
    }
}

class StompPrincipal implements Principal {
    private final String name;

    public StompPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}