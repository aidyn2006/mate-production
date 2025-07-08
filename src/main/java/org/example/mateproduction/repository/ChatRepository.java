package org.example.mateproduction.repository;

import org.example.mateproduction.dto.response.ChatPreviewResponse;
import org.example.mateproduction.entity.Chat;
import org.example.mateproduction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    // Optimized for consistent participant order
    @Query("""
    SELECT c FROM Chat c
    WHERE (c.participant1 = :user1 AND c.participant2 = :user2)
       OR (c.participant1 = :user2 AND c.participant2 = :user1)
""")
    Optional<Chat> findByParticipants(@Param("user1") User user1, @Param("user2") User user2);

    // The new, highly efficient query for chat previews
    @Query("""
        SELECT new org.example.mateproduction.dto.response.ChatPreviewResponse(
            c.id,
            CASE WHEN c.participant1.id = :currentUserId THEN c.participant2.id ELSE c.participant1.id END,
            CASE WHEN c.participant1.id = :currentUserId THEN CONCAT(c.participant2.name, ' ', c.participant2.surname) ELSE CONCAT(c.participant1.name, ' ', c.participant1.surname) END,
            CASE WHEN c.participant1.id = :currentUserId THEN c.participant2.avatarUrl ELSE c.participant1.avatarUrl END,
            (SELECT m.content FROM Message m WHERE m.chat = c ORDER BY m.createdAt DESC LIMIT 1),
            (SELECT m.createdAt FROM Message m WHERE m.chat = c ORDER BY m.createdAt DESC LIMIT 1),
            (SELECT COUNT(m) > 0 FROM Message m WHERE m.chat = c AND m.sender.id != :currentUserId AND m.isRead = false)
        )
        FROM Chat c
        WHERE c.participant1.id = :currentUserId OR c.participant2.id = :currentUserId
        ORDER BY (SELECT m.createdAt FROM Message m WHERE m.chat = c ORDER BY m.createdAt DESC LIMIT 1) DESC
    """)
    List<ChatPreviewResponse> findChatPreviewsByUser(@Param("currentUserId") UUID currentUserId);

    @Query("SELECT c FROM Chat c WHERE c.participant1 = :user OR c.participant2 = :user")
    List<Chat> findAllByUser(@Param("user") User user);
}