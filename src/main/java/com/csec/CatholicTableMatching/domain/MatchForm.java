package com.csec.CatholicTableMatching.domain;

import lombok.Data;

@Data
public class MatchForm{

    private Customer user;
    private String Foodtype;
    private String timeSlot;

}