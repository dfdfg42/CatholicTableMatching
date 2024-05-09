package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String gender;     // 'M' for male, 'F' for female

    private boolean matched = false;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_form_id", referencedColumnName = "id")
    private MatchForm matchForm;

}