package com.csec.CatholicTableMatching;

import com.csec.CatholicTableMatching.security.domain.User;

import java.util.List;

class TreeNode {
    String key;
    List<User> users;
    TreeNode left;
    TreeNode right;

    public TreeNode(String key, List<User> users) {
        this.key = key;
        this.users = users;
        this.left = null;
        this.right = null;
    }
}
