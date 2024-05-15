package com.csec.CatholicTableMatching.domain;

import com.csec.CatholicTableMatching.security.domain.User;
import jakarta.persistence.*;
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
    private String preferGender;

    public MatchForm(){
    }
    public MatchForm(User user, String foodType, String timeSlot, String preferGender) {
        this.user = user;
        this.foodType = foodType;
        this.timeSlot = timeSlot;
        this.preferGender = preferGender;
    }
}