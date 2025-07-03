package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chats")
public class Chat extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "participant1_id", nullable = false)
    private User participant1;

    @ManyToOne
    @JoinColumn(name = "participant2_id", nullable = false)
    private User participant2;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;
}