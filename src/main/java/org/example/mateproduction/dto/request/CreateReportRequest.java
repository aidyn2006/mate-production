package org.example.mateproduction.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.mateproduction.util.ReportReason;
import org.example.mateproduction.util.ReportableType;

import java.util.UUID;

@Data
public class CreateReportRequest {

    @NotNull(message = "Reported entity ID cannot be null.")
    private UUID reportedEntityId;

    @NotNull(message = "Reported entity type cannot be null.")
    private ReportableType reportedEntityType;

    @NotNull(message = "Reason for reporting cannot be null.")
    private ReportReason reason;

    @Size(max = 2000, message = "Description can be up to 2000 characters.")
    private String description;
}