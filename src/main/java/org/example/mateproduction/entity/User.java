package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.AuthProvider;
import org.example.mateproduction.util.Role;
import org.example.mateproduction.util.UserStatus;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User extends BaseEntity {

    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean isVerified = false;
    private String avatarUrl;

    private Boolean isDeleted =false;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE; // New field with default value
    private String banReason; // New field

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    @Column(name = "is_two_fa_enabled")
    private Boolean isTwoFaEnabled;

    @Column(name = "two_fa_secret")
    private String twoFaSecret;


}
