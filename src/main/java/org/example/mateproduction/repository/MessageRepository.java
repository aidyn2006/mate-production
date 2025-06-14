package org.example.mateproduction.repository;

import org.example.mateproduction.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.chat.sender.id = :senderId AND m.chat.receiver.id = :receiverId) OR " +
            "(m.chat.sender.id = :receiverId AND m.chat.receiver.id = :senderId)")
    List<Message> findChatMessages(@Param("senderId") UUID senderId,
                                   @Param("receiverId") UUID receiverId);


}
