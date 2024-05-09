package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MatchForm{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "matchForm")
    private User user;

    private String foodType;
    private String timeSlot;
    private String gender;
    private String preferGender;

}