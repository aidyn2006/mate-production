package org.example.mateproduction.service;

import org.example.mateproduction.util.Status;

import java.util.UUID;

public interface Moderatable {
    UUID getId();
    void setStatus(Status status);
    void setModerationComment(String comment);
    void setFeatured(boolean featured);
}