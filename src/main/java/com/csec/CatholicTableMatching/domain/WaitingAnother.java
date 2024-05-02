package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Setter
@Getter
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