package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;

@Entity
public class WaitingSame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    private User user;

}
