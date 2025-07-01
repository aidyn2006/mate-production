package org.example.mateproduction.dto.request;

import lombok.Data;

@Data
public class Verify2FARequest {
    private String email;
    private Integer code;
}