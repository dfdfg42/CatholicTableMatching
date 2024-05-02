package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class WaitingAnother {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne(fetch = FetchType.LAZY)
    private Customer user;

    private String foodType;

    private Date matchDate;

    private String timeSlot; // "Morning", "Afternoon", "Evening"






}
// 남 여 소개팅용