package org.example.mateproduction.service;

import org.example.mateproduction.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface AdminUserService {

    void banUser(UUID userId);
    void unbanUser(UUID userId);



}
