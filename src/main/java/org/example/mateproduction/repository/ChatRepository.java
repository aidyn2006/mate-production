package org.example.mateproduction.repository;

import org.example.mateproduction.entity.Chat;
import org.example.mateproduction.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Optional<Chat> findBySenderAndReceiver(User sender, User receiver);

}
