package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.ReportReason;
import org.example.mateproduction.util.ReportStatus;
import org.example.mateproduction.util.ReportableType;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reports")
public class Report extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Column(name = "reported_entity_id", nullable = false)
    private UUID reportedEntityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reported_entity_type", nullable = false)
    private ReportableType reportedEntityType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_admin_id")
    private User resolvedBy;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;
}