package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user2;

    private Date matchDate;

    private String timeSlot; // "Morning", "Afternoon", "Evening"

    private String foodType;

    // Other fields and methods
}