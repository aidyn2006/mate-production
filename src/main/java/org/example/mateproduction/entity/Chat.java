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
public class Chat extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User receiver;

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User sender;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Message> messages;
}
