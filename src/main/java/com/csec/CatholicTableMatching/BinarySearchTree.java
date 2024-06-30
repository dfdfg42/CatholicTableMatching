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

    public void insert(String foodType, String timeSlot, String preferGender, User user) {
        root = insertRec(root, foodType, timeSlot, preferGender, user);
    }

    private TreeNode insertRec(TreeNode root, String foodType, String timeSlot, String preferGender, User user) {
        if (root == null) {
            return new TreeNode(foodType, timeSlot, preferGender, user);
        }
        int cmp = compare(root, foodType, timeSlot, preferGender);
        if (cmp > 0) {
            root.left = insertRec(root.left, foodType, timeSlot, preferGender, user);
        } else if (cmp < 0) {
            root.right = insertRec(root.right, foodType, timeSlot, preferGender, user);
        } else {
            root.users.add(user);
        }
        return root;
    }

    public void remove(String foodType, String timeSlot, String preferGender, User user) {
        root = removeRec(root, foodType, timeSlot, preferGender, user);
    }

    private TreeNode removeRec(TreeNode root, String foodType, String timeSlot, String preferGender, User user) {
        if (root == null) return null;

        int cmp = compare(root, foodType, timeSlot, preferGender);
        if (cmp > 0) {
            root.left = removeRec(root.left, foodType, timeSlot, preferGender, user);
        } else if (cmp < 0) {
            root.right = removeRec(root.right, foodType, timeSlot, preferGender, user);
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
                root.foodType = temp.foodType;
                root.timeSlot = temp.timeSlot;
                root.preferGender = temp.preferGender;
                root.users = temp.users;
                root.right = removeRec(root.right, temp.foodType, temp.timeSlot, temp.preferGender, user);
            }
        }
        return root;
    }

    private TreeNode findMin(TreeNode root) {
        while (root.left != null) root = root.left;
        return root;
    }

    public List<User> search(String foodType, String timeSlot, String preferGender) {
        return searchRec(root, foodType, timeSlot, preferGender);
    }

    private List<User> searchRec(TreeNode root, String foodType, String timeSlot, String preferGender) {
        if (root == null) return new ArrayList<>();

        int cmp = compare(root, foodType, timeSlot, preferGender);
        if (cmp == 0) {
            return root.users;
        } else if (cmp > 0) {
            return searchRec(root.left, foodType, timeSlot, preferGender);
        } else {
            return searchRec(root.right, foodType, timeSlot, preferGender);
        }
    }

    private int compare(TreeNode node, String foodType, String timeSlot, String preferGender) {
        int cmpFoodType = foodType.compareTo(node.foodType);
        if (cmpFoodType != 0) return cmpFoodType;
        int cmpTimeSlot = timeSlot.compareTo(node.timeSlot);
        if (cmpTimeSlot != 0) return cmpTimeSlot;
        return preferGender.compareTo(node.preferGender);
    }
}

