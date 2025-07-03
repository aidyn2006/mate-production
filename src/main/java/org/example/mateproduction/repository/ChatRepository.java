package org.example.mateproduction.repository;

import org.example.mateproduction.entity.Chat;
import org.example.mateproduction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {

    @Query("SELECT c FROM Chat c WHERE (c.participant1 = :p1 AND c.participant2 = :p2) OR (c.participant1 = :p2 AND c.participant2 = :p1)")
    Optional<Chat> findChatByParticipants(@Param("p1") User p1, @Param("p2") User p2);

    @Query("SELECT c FROM Chat c WHERE c.participant1 = :user OR c.participant2 = :user")
    List<Chat> findAllByUser(@Param("user") User user);
}