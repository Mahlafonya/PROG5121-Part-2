/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project;

/**
 *
 * @author RC_Student_lab - Matsobane Mahlafonya - ST10470123
 */
public class Validation {
        public static boolean checkUserName(String username) {
        boolean hasSpecial = username.contains("_");
        boolean hasValidLength = username.length() <= 5;

        return hasValidLength && hasSpecial;
    }

    public static boolean checkPhoneNumber(String phoneNumber) {
        // Check if the phone number is in the format +27XXXXXXXXX or 0XXXXXXXXX
        //trim number to remove any leading or trailing spaces
        phoneNumber = phoneNumber.trim();
        return phoneNumber.matches("^\\+27\\d{9}$") || phoneNumber.matches("^0[6-8]\\d{8}$");
    }

    public static boolean checkPasswordComplexity(String password) {
        if (password.length() < 8) return false;

        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasDigit = password.matches(".*\\d.*"); // used chatgpt to generate regex
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"); // used chatgpt to generate regex

        System.out.println(hasUppercase+" "+hasDigit +" "+ hasSpecial);

        return hasUppercase && hasDigit && hasSpecial;
    }
}
