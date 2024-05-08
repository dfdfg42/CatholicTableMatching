package com.csec.CatholicTableMatching.domain;

import lombok.Data;

@Data
public class MatchForm{

    private User user;
    private String Foodtype;
    private String timeSlot;
    private String name;
    private String gender;

}