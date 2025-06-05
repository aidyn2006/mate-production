package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    private User reviewer;

    @ManyToOne
    private User user;

    @ManyToOne
    private Ad advertisement;

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}