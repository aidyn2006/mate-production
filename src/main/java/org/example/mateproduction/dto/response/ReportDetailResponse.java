package org.example.mateproduction.dto.response;

import lombok.Builder;
import lombok.Data;
import org.example.mateproduction.util.ReportReason;
import org.example.mateproduction.util.ReportStatus;
import org.example.mateproduction.util.ReportableType;

import java.time.LocalDateTime;
import java.util.UUID;
// ... other imports

@Data
@Builder
public class ReportDetailResponse {
    private UUID id;
    private UserResponse reporter;
    private UUID reportedEntityId;
    private ReportableType reportedEntityType;
    private ReportReason reason;
    private String description;
    private ReportStatus status;
    private UUID resolvedByAdminId;
    private String resolutionNotes;
    private LocalDateTime createdAt;
    private Object reportedContent;
}