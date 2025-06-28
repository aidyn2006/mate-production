package org.example.mateproduction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AuditLog extends BaseEntity {

    private String email;
    @Column(columnDefinition = "TEXT")
    private String action;
    @Column(columnDefinition = "TEXT")
    private String details;




}
