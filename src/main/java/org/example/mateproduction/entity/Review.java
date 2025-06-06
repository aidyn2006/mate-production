package org.example.mateproduction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.mateproduction.entity.base.BaseEntity;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Review  extends BaseEntity {

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