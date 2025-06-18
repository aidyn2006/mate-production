package org.example.mateproduction.entity;

import jakarta.persistence.*;
import org.example.mateproduction.entity.base.BaseEntity;
import org.example.mateproduction.util.ReportReason;
import org.example.mateproduction.util.ReportStatus;

// New Entity: Report.java
public class Report extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter; // The user who made the report

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private User reportedUser; // The user being reported (optional)

    @ManyToOne
    @JoinColumn(name = "reported_ad_house_id")
    private AdHouse reportedAdHouse; // The house ad being reported (optional)

    @ManyToOne
    @JoinColumn(name = "reported_ad_seeker_id")
    private AdSeeker reportedAdSeeker; // The seeker ad being reported (optional)

    @Enumerated(EnumType.STRING)
    private ReportReason reason; // e.g., SPAM, INAPPROPRIATE_CONTENT, SCAM

    @Column(columnDefinition = "TEXT")
    private String description; // User's custom description of the issue

    @Enumerated(EnumType.STRING)
    private ReportStatus status; // e.g., PENDING, REVIEWED, RESOLVED

    @ManyToOne
    @JoinColumn(name = "resolved_by_admin_id")
    private User resolvedBy; // The admin who handled the report

    private String resolutionNotes; // Admin's notes on the action taken
}
