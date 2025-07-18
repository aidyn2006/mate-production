package org.example.mateproduction.config;

import org.example.mateproduction.config.Jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
//@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtService jwtService; // Ваш сервис для работы с JWT

    @Autowired
    private UserDetailsService userService; // Сервис для получения пользователей

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Broker for broadcasting messages to clients on specific destinations
        config.enableSimpleBroker("/topic", "/queue", "/user");
        // Prefix for messages bound for @MessageMapping-annotated methods
        config.setApplicationDestinationPrefixes("/app");
        // Prefix for user-specific destinations
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/ws") // Match the frontend's request URL
                .setAllowedOriginPatterns(
                        "http://localhost:*",
                        "http://127.0.0.1:*",
                        "https://animated-salamander-7746f5.netlify.app" // ✅ нужен именно этот
                )
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
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Authenticate only on CONNECT command
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String jwt = authHeader.substring(7);
                        try {
                            String userEmail = jwtService.extractUsername(jwt);
                            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                                UserDetails userDetails = userService.loadUserByUsername(userEmail);
                                if (jwtService.isTokenValid(jwt, userDetails)) {
                                    // Set the user for the WebSocket session
                                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());
                                    accessor.setUser(authToken);
                                    System.out.println("WebSocket Authenticated User: " + userEmail);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("WebSocket authentication failed: " + e.getMessage());
                            // Optionally, you can throw an exception here to reject the connection
                        }
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