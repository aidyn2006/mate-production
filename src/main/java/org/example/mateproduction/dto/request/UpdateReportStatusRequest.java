package org.example.mateproduction.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.mateproduction.util.ReportStatus;

@Data
public class UpdateReportStatusRequest {

    @NotNull(message = "Report status cannot be null.")
    private ReportStatus status;

    @Size(max = 2000, message = "Resolution notes can be up to 2000 characters.")
    private String resolutionNotes;
}