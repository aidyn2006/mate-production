package org.example.mateproduction.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
@Builder
public class ChatPreviewResponse {
    private UUID chatId;
    private UUID companionId;
    private String companionName;
    private String companionAvatarUrl;
    private String lastMessage;
    private Date lastMessageTime;
    private Boolean hasUnreadMessages;
}
