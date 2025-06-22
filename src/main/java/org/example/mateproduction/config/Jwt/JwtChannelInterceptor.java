package org.example.mateproduction.config.Jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // Always try to get the principal from the session accessor first.
        // This is where it should be after a successful CONNECT.
        Principal sessionPrincipal = accessor.getUser();

        if (sessionPrincipal instanceof Authentication authentication) {
            // If the principal is already set in the accessor and is an Authentication object,
            // then set it in the SecurityContextHolder for the current thread.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("WebSocket AUTH: Principal '{}' re-established in SecurityContextHolder for command {}. Session: {}",
                    authentication.getName(), command, accessor.getSessionId());
        } else {
            // If no valid Principal is found in the session accessor,
            // we try to authenticate from the Authorization header.
            // This is primarily for CONNECT, but acts as a fallback for others if needed.
            List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");
            if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
                String fullToken = authorizationHeaders.get(0);
                if (fullToken != null && fullToken.startsWith("Bearer ")) {
                    String token = fullToken.substring(7);
                    try {
                        Authentication authentication = jwtService.getAuthentication(token);
                        if (authentication != null && authentication.isAuthenticated()) {
                            // For CONNECT, set the user on the accessor for session persistence.
                            // For other commands, if we re-authenticate here, it implies accessor.getUser() failed,
                            // so we should also set it for future messages in the session.
                            accessor.setUser(authentication);
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            log.info("WebSocket AUTH: User '{}' authenticated via JWT token for command {}. Session: {}",
                                    authentication.getName(), command, accessor.getSessionId());
                        } else {
                            log.warn("WebSocket AUTH: Authentication object from token is not valid or not authenticated for command {}. Token: {}", command, token);
                            SecurityContextHolder.clearContext();
                            if (StompCommand.CONNECT.equals(command)) {
                                throw new BadCredentialsException("Invalid or unauthenticated JWT token for WebSocket connection.");
                            }
                        }
                    } catch (Exception e) {
                        log.error("WebSocket AUTH ERROR: Failed to authenticate JWT token '{}' during {}: {}", token, command, e.getMessage());
                        SecurityContextHolder.clearContext();
                        if (StompCommand.CONNECT.equals(command)) {
                            throw new BadCredentialsException("Authentication failed for WebSocket connection: " + e.getMessage(), e);
                        }
                    }
                } else {
                    log.warn("WebSocket AUTH: Authorization header found but not in 'Bearer <token>' format for command {}: {}", command, fullToken);
                    SecurityContextHolder.clearContext();
                    if (StompCommand.CONNECT.equals(command)) {
                        throw new BadCredentialsException("Malformed Authorization header for WebSocket connection.");
                    }
                }
            } else {
                // This is the warning you're seeing for SUBSCRIBE/SEND if accessor.getUser() is null.
                log.warn("WebSocket AUTH: No Authorization header and no principal found in session for STOMP command '{}'. Session: {}", command, accessor.getSessionId());
                SecurityContextHolder.clearContext();
                // We don't throw an exception here for non-CONNECT commands,
                // allowing the controller to handle a null Principal if needed.
            }
        }

        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // Clear the SecurityContextHolder after processing if it's not a CONNECT command.
        // The principal is stored in the session accessor, so it doesn't need to stay in thread-local context.
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            SecurityContextHolder.clearContext();
            log.debug("WebSocket AUTH: SecurityContextHolder cleared after postSend for command {}. Session: {}",
                    accessor.getCommand(), accessor.getSessionId());
        }
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // Clear the SecurityContextHolder after processing if it's not a CONNECT command.
        if (!StompCommand.CONNECT.equals(accessor.getCommand())) {
            SecurityContextHolder.clearContext();
            log.debug("WebSocket AUTH: SecurityContextHolder cleared after receive completion for command {}. Session: {}",
                    accessor.getCommand(), accessor.getSessionId());
        }
    }
}