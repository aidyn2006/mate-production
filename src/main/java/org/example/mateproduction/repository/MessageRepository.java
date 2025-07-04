package org.example.mateproduction.repository;

import org.example.mateproduction.entity.Chat;
import org.example.mateproduction.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE m.chat = :chat ORDER BY m.createdAt ASC")
    List<Message> findByChatOrderByCreatedAtAsc(@Param("chat") Chat chat);

    @Modifying // Required for UPDATE or DELETE queries
    @Query("UPDATE Message m SET m.isRead = true WHERE m.chat.id = :chatId AND m.sender.id = :senderId AND m.isRead = false")
    void markAllAsReadInChat(@Param("chatId") UUID chatId, @Param("senderId") UUID senderId);
}