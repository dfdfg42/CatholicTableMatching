package com.csec.CatholicTableMatching;

import com.csec.CatholicTableMatching.security.domain.User;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
    String foodType;
    String timeSlot;
    String preferGender;
    List<User> users;
    TreeNode left, right;

    public TreeNode(String foodType, String timeSlot, String preferGender, User user) {
        this.foodType = foodType;
        this.timeSlot = timeSlot;
        this.preferGender = preferGender;
        this.users = new ArrayList<>();
        this.users.add(user);
        this.left = this.right = null;
    }
}

