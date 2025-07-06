package org.example.mateproduction.service;

import org.example.mateproduction.config.Jwt.JwtUserDetails;
import org.example.mateproduction.dto.request.MessageRequest;
import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.dto.response.MessageResponse;
import org.example.mateproduction.entity.Chat;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.exception.NotFoundException;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

public interface MessageService {
//    MessageResponse sendMessage(MessageRequest request,String email) throws NotFoundException;
//    List<MessageResponse> getChatHistory(UUID senderId, UUID receiverId) throws NotFoundException;
//    void deleteMessage(UUID messageId) throws NotFoundException;
//    void clearChat(UUID senderId, UUID receiverId);
//    List<ChatPreviewResponse> getUserChats(UUID currentUserId) throws NotFoundException;
//    void markMessagesAsRead(UUID senderId, UUID receiverId);
//    MessageResponse saveAndSendMessage(MessageRequest request, Principal principal) throws NotFoundException;
//    List<MessageResponse> getChatHistory(UUID companionId, Principal principal) throws NotFoundException;
//    List<ChatPreviewResponse> getUserChats(Principal principal) throws NotFoundException;
//    void markMessagesAsRead(UUID companionId, Principal principal) throws NotFoundException;
    void saveAndSendMessage(MessageRequest request, Principal principal) throws NotFoundException;
//    Chat findOrCreateChat(User user1, User user2);
    List<MessageResponse> getChatHistory(UUID companionId, Principal principal) throws NotFoundException;
    List<ChatPreviewResponse> getUserChats(Principal principal) throws NotFoundException;
    void markMessagesAsRead(UUID companionId, Principal principal) throws NotFoundException;
//    User findUserByPrincipal(Principal principal) throws NotFoundException;

}