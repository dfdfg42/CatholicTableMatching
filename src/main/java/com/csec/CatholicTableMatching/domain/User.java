package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name="TUSER")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String gender;     // 'M' for male, 'F' for female

    @OneToOne(mappedBy = "user")
    private WaitingAnother waitingAnother;

    @OneToMany(mappedBy = "user1")
    private List<Match> matchesAsUser1;

    @OneToMany(mappedBy = "user2")
    private List<Match> matchesAsUser2;




}