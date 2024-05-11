package com.csec.CatholicTableMatching.security.domain;

import com.csec.CatholicTableMatching.domain.MatchForm;
import jakarta.persistence.*;
import lombok.*;

@Entity (name = "CUSER")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String provider; // 소셜로그인 제공자

    private String name;

    private String loginId;

    private UserRole role;

    private String gender;     // 'M' for male, 'F' for female

    private String phoneNum;

    private boolean matched = false;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "match_form_id", referencedColumnName = "id")
    private MatchForm matchForm;
}
