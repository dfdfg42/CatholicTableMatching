package com.csec.CatholicTableMatching;

import com.csec.CatholicTableMatching.security.domain.User;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class BinarySearchTree {
    private TreeNode root;

    public BinarySearchTree() {
        this.root = null;
    }

    public void insert(String key, User user) {
        root = insertRec(root, key, user);
    }

    private TreeNode insertRec(TreeNode root, String key, User user) {
        if (root == null) {
            List<User> users = new ArrayList<>();
            users.add(user);
            return new TreeNode(key, users);
        }
        if (key.compareTo(root.key) < 0) {
            root.left = insertRec(root.left, key, user);
        } else if (key.compareTo(root.key) > 0) {
            root.right = insertRec(root.right, key, user);
        } else {
            root.users.add(user);
        }
        return root;
    }

    public void remove(String key, User user) {
        root = removeRec(root, key, user);
    }

    private TreeNode removeRec(TreeNode root, String key, User user) {
        if (root == null) return null;

        if (key.compareTo(root.key) < 0) {
            root.left = removeRec(root.left, key, user);
        } else if (key.compareTo(root.key) > 0) {
            root.right = removeRec(root.right, key, user);
        } else {
            Iterator<User> iterator = root.users.iterator();
            while (iterator.hasNext()) {
                User u = iterator.next();
                if (u.equals(user)) {
                    iterator.remove();
                    break;
                }
            }
            if (root.users.isEmpty()) {
                if (root.left == null) return root.right;
                if (root.right == null) return root.left;
                TreeNode temp = findMin(root.right);
                root.key = temp.key;
                root.users = temp.users;
                root.right = removeRec(root.right, temp.key, user);
            }
        }
        return root;
    }

    private TreeNode findMin(TreeNode root) {
        while (root.left != null) root = root.left;
        return root;
    }

    public List<User> search(String key) {
        TreeNode node = searchRec(root, key);
        return node != null ? node.users : null;
    }

    private TreeNode searchRec(TreeNode root, String key) {
        if (root == null || root.key.equals(key)) {
            return root;
        }
        if (key.compareTo(root.key) < 0) {
            return searchRec(root.left, key);
        }
        return searchRec(root.right, key);
    }
}
