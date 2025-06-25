package org.example.mateproduction.entity.base;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.mateproduction.entity.User;
import org.example.mateproduction.util.Status;

@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class Ad extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "moderation_comment")
    private String moderationComment;

    @Column(nullable = false)
    private boolean featured = false;
}