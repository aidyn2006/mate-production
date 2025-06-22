package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review")
public class Review extends BaseEntity {

    @ManyToOne(optional = false)
    private User reviewer;

    @ManyToOne(optional = false)
    private User user; // заменяет adSeeker

    @ManyToOne(optional = false)
    private AdHouse advertisement; // заменяет adHouse

    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;
}

