/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.project;

import java.util.*;

import com.project.Model.Message;
import com.project.Model.User;

/**
 *
 * @author RC_Student_lab
 */

public class Main {
    static List<User> users = new ArrayList<>();
    static List<Message> messages = new ArrayList<>();

    static User loggedUser = null;
    public static String registerUser(String username, String firstname, String lastname, String password, String phonenumber) {
        boolean isRegistered = false;
        String result = "";
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                isRegistered = true;
                break;
            }
        }

        if (Validation.checkUserName(username) == false) {
            result += "\nUsername is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in lenght";
        } else if (Validation.checkPasswordComplexity(password) == false) {
            result +=  "\nPassword is incorrectly formatted; please ensure that the password contains at least characters, a capital letter, a number, or a special character";
        } else if(Validation.checkPhoneNumber(phonenumber) == false) {
            result +=  "\nCell phone number is incorrectly formatted or does not contain international code";
        } else{
            if (isRegistered == false) {
                User newUser = new User(username, firstname, lastname, password, phonenumber);
                users.add(newUser);

                result =  "User successfully registered.";
            } else if(isRegistered == true) {
                result =  "Username already exists. Please try again.";
            }
        }
        return result;
    }

    public static boolean loginUser(String username, String password) {
        boolean result = false;
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                loggedUser = user;
                result = true;
            }
        }
        return result;
    }

    private static String requestUsernameWithRetry(String expectation, Scanner input) {
        System.out.println("\n" + expectation + "\n");
        String result;
        while (true) {
            System.out.print("Enter username: ");
            result = input.nextLine();

            if (Validation.checkUserName(result)) {
                return result;
            }

            System.out.println("\n\nUsername is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.\n");
        }
    }

    private static String requestPasswordWithRetry(String expectation, Scanner input) {
        System.out.println("\n" + expectation + "\n");
        String result;
        while (true) {
            System.out.print("Enter password: ");
            result = input.nextLine();

            if (Validation.checkPasswordComplexity(result)) {
                return result;
            }

            System.out.println("\n\nPassword is incorrectly formatted; please ensure that the password contains at least characters, a capital letter, a number, or a special character\n");
        }
    }


    private static String requestPhoneNumberWithRetry(String expectation, Scanner input) {
        System.out.println("\n" + expectation + "\n");
        String result;
        while (true) {
            System.out.print("Enter phone number: ");
            result = input.nextLine();

            if (Validation.checkPhoneNumber(result)) {
                return result;
            }

            System.out.println("\n\nCell phone number is incorrectly formatted or does not contain international code\n");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\nChoose an option: 1 - Register, 2 - Login, 3 - Exit");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    String usernameReqirement = "Username must contain an underscore and not more than five characters in length";

                    String regUsername = requestUsernameWithRetry(usernameReqirement, sc);


                    System.out.print("Enter your first name (ie. John): ");
                    String regFirstname = sc.nextLine();

                    System.out.print("Enter your last name (ie. Doe): ");
                    String regLastname = sc.nextLine();

                    String passwordRequirement = "\n Password must be: "
                            +"\n - At least eight characters in length"
                            +"\n - Contains a capital letter"
                            +"\n - Contains a number"
                            +"\n - Contains a special character";

                    String regPassword = requestPasswordWithRetry(passwordRequirement, sc);

                    String requestPhonenumberRequirement = "\n The cell phone number must contain international code, followed by numbers no more than ten or less than 9.";
                    String regPhone = requestPhoneNumberWithRetry(requestPhonenumberRequirement, sc);

                    String registering = registerUser(regUsername, regFirstname, regLastname, regPassword, regPhone);
                    System.out.println(registering);

                    break;

                case 2:
                    System.out.print("Enter username: ");
                    String loginUsername = sc.nextLine();

                    System.out.print("Enter password: ");
                    String loginPassword = sc.nextLine();

                    boolean isLoggedIn = loginUser(loginUsername, loginPassword);
                    if (isLoggedIn) {
                        System.out.println("Welcome QuickChat");
                        RunMessaging.startMessaging(loggedUser, users, messages, sc);
                    } else {
                        System.out.println("User name or password incorrect, please try again.");
                    }
                    break;

                case 3:
                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}