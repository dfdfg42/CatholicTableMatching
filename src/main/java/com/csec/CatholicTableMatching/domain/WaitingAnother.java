package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;

@Entity
public class WaitingAnother {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User user;




}
