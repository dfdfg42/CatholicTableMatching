package com.csec.CatholicTableMatching.domain;

import com.csec.CatholicTableMatching.security.domain.User;
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


    private boolean sended = false;

    // Other fields and methods


    public Match(User user1, User user2, Date matchDate, String timeSlot, String foodType) {
        this.user1 = user1;
        this.user2 = user2;
        this.matchDate = matchDate;
        this.timeSlot = timeSlot;
        this.foodType = foodType;
    }

    public Match() {
    }
}