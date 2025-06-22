package org.example.mateproduction.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.Role;

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
}
