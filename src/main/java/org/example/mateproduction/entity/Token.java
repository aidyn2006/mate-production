package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.TokenType;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tokens")
public class Token extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}