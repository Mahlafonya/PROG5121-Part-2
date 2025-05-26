/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project;

import org.junit.Before;
import org.junit.Test;
import java.util.*;


import com.project.UserRegisterAndLogin;
import com.project.Validation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author RC_Student_lab
 */
public class TestUserRegisterAndLogin {

    @Before
    public void setUp() {
        List<HashMap<String, String>> users = getUsersList();
        users.clear();
    }

    @Test
    public void testCheckUserName() {
        assertTrue(Validation.checkUserName("User_"));
        assertFalse(Validation.checkUserName("user123"));
        assertFalse(Validation.checkUserName("Us3!"));
    }

    @Test
    public void checkPasswordComplexity() {
        assertTrue(Validation.checkPasswordComplexity("Strong1@"));
        assertFalse(Validation.checkPasswordComplexity("weakpass"));
        assertFalse(Validation.checkPasswordComplexity("Short1!"));
    }

    @Test
    public void testCheckPhoneNumber() {
        assertTrue(Validation.checkPhoneNumber("+27831234567"));
        assertFalse(Validation.checkPhoneNumber("0831234567"));
        assertFalse(Validation.checkPhoneNumber("+271234"));
    }

    @Test
    public void testRegisterAndLoginSuccess() {
        String registered = UserRegisterAndLogin.registerUser("U_ser", "John", "Doe", "Password1@", "+27831234567");
        assertEquals("User successfully registered.",registered);

        boolean loggedIn = UserRegisterAndLogin.loginUser("U_ser", "Password1@");
        assertTrue(loggedIn);
    }

    @Test
    public void testDuplicateRegistrationFails() {
        UserRegisterAndLogin.registerUser("User123!", "John", "Doe", "Password1@", "+27831234567");
        String secondAttempt = UserRegisterAndLogin.registerUser("User123!", "John", "Doe", "Password1@", "+27831234567");
        assertNotEquals("User successfully registered.", secondAttempt);
    }

    @Test
    public void testLoginFailsWithWrongCredentials() {
        UserRegisterAndLogin.registerUser("User123!", "John", "Doe", "Password1@", "+27831234567");

        assertFalse(UserRegisterAndLogin.loginUser("User123!", "WrongPass"));
        assertFalse(UserRegisterAndLogin.loginUser("WrongUser", "Password1@"));
    }

    private List<HashMap<String, String>> getUsersList() {
        try {
            java.lang.reflect.Field field = UserRegisterAndLogin.class.getDeclaredField("users");
            field.setAccessible(true);
            return (List<HashMap<String, String>>) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

