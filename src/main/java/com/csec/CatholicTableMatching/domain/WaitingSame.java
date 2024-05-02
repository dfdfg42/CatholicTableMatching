package com.csec.CatholicTableMatching.domain;

import jakarta.persistence.*;

@Entity
public class WaitingSame {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;


}
// 동성 밥용 일단 냅둠