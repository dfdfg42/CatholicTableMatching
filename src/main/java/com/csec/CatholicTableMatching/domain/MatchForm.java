package com.csec.CatholicTableMatching.domain;

import com.csec.CatholicTableMatching.security.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "match_time_slots", joinColumns = @JoinColumn(name = "match_form_id"))
    @Column(name = "time_slot")
    private List<Integer> timeSlots;  // 여러 시간을 저장하는 리스트

    public MatchForm(){
    }
    public MatchForm(User user, String foodType, String timeSlot, String preferGender) {
        this.user = user;
        this.foodType = foodType;
        this.timeSlot = timeSlot;
        this.preferGender = preferGender;
    }
}