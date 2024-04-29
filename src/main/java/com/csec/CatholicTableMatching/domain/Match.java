package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user1;
    @ManyToOne
    private User user2;
    private Date matchDate;
    // Other fields and methods
}