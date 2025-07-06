package org.example.mateproduction.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class ChatPreviewResponse {
    public ChatPreviewResponse(UUID chatId, UUID companionId, String companionName, String companionAvatarUrl, String lastMessage, Date lastMessageTime, boolean hasUnreadMessages) {
        this.chatId = chatId;
        this.companionId = companionId;
        this.companionName = companionName;
        this.lastMessage = lastMessage != null ? lastMessage : "No messages yet.";
        this.lastMessageTime = lastMessageTime;
        this.hasUnreadMessages = hasUnreadMessages;
    }

    private UUID chatId;
    private UUID companionId;
    private String companionName;
    private String companionAvatarUrl;
    private String lastMessage;
    private Date lastMessageTime;
    private Boolean hasUnreadMessages;
}
