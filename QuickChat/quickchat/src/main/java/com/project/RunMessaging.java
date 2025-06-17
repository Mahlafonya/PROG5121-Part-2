package com.project;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.project.Model.Message;
import com.project.Model.User;

public class RunMessaging {
    public static void startMessaging(User loggedUser, List<User> users, List<Message> messages, Scanner sc) {

        boolean messagingMenu = true;
        while (messagingMenu) {
            System.out.println("\n--- Messaging Menu ---");
            System.out.println("1 - Send New Message");
            System.out.println("2 - View Your Received Messages"); 
            System.out.println("3 - View Stats");
            System.out.println("4 - Message Centre");
            System.out.println("5 - Logout");
            System.out.print("Choose an option: ");

            int msgChoice = -1;
            try {
                msgChoice = sc.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number from the menu options.");
                sc.nextLine();
                continue;
            }
            sc.nextLine();

            switch (msgChoice) {
                case 1: // Send New Message
                    System.out.print("Enter recipient username: ");
                    String recipientUsername = sc.nextLine();

                    User recipientUser = null;
                    for (User u : users) {
                        if (u.getUsername().equals(recipientUsername)) {
                            recipientUser = u;
                            break;
                        }
                    }

                    if (recipientUser != null) {
                        System.out.print("Enter message content: ");
                        String msgText = sc.nextLine();
                        String recipientCell = recipientUser.getPhoneNumber();
                        String senderCell = loggedUser.getPhoneNumber();
                        System.out.print("Choose what to do with this message (send/store/discard): ");
                        String decision = sc.nextLine();

                        Message msg = new Message(senderCell, recipientCell, msgText, decision);

                        if (!msg.checkMessageID()) {
                            System.out.println("Error: Generated message ID is too long. Message not processed.");
                            break;
                        }

                        String statusMessage = msg.sentMessage(decision);
                        System.out.println(statusMessage);

                        Message.storeMessage(List.of(msg));

                        String hash = msg.createMessageHash();
                        if (hash != null) {
                            System.out.println("Message hash: " + hash);
                        } else {
                            System.out.println("Failed to generate message hash.");
                        }

                    } else {
                        System.out.println("Recipient username not found. Please ensure the username exists before sending.");
                    }
                    break;

                case 2: // View Your Received Messages
                    System.out.println("\n--- Your Received Messages (In-memory Filtered) ---\n");
                    System.out.println(Message.printMessages(messages));
                    break;

                case 3: // View Total Messages Created/Processed
                    System.out.println("Total messages (including sent, stored, disregarded, loaded from file): " + Message.returnTotalMessages());
                    break;

                case 4: // Message Centre - New sub-menu for advanced operations
                    boolean messageCentreMenu = true;
                    while (messageCentreMenu) {
                        System.out.println("\n--- Message Centre ---");
                        System.out.println("1 - Display All Sent Messages Details");
                        System.out.println("2 - Display Longest Message Content (From all messages)");
                        System.out.println("3 - Search Message by ID");
                        System.out.println("4 - Search Messages by Recipient");
                        System.out.println("5 - Delete Message by Hash");
                        System.out.println("6 - Generate Sent Messages Report");
                        System.out.println("7 - Back to Main Messaging Menu");
                        System.out.print("Choose an option: ");

                        int mcChoice = -1;
                        try {
                            mcChoice = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a number from the menu options.");
                            sc.nextLine();
                            continue;
                        }
                        sc.nextLine();

                        switch (mcChoice) {
                            case 1:
                                System.out.println(Message.displaySentMessagesDetails());
                                break;
                            case 2:
                                System.out.println("Longest Message Content: " + Message.getLongestMessageContent());
                                break;
                            case 3:
                                System.out.print("Enter Message ID to search: ");
                                String searchId = sc.nextLine();
                                System.out.println(Message.searchMessageById(searchId));
                                break;
                            case 4:
                                System.out.print("Enter Recipient Phone Number to search (e.g., +2783...): ");
                                String searchRecipient = sc.nextLine();
                                System.out.println(Message.searchMessagesByRecipient(searchRecipient));
                                break;
                            case 5:
                                System.out.print("Enter Message Hash to delete: ");
                                String deleteHash = sc.nextLine();
                                System.out.println(Message.deleteMessageByHash(deleteHash));
                                break;
                            case 6:
                                System.out.println(Message.generateSentMessagesReport());
                                break;
                            case 7:
                                System.out.println("Returning to main messaging menu...");
                                messageCentreMenu = false; // Exit Message Centre sub-menu
                                break;
                            default:
                                System.out.println("Invalid choice. Please select an option between 1 and 7.");
                        }
                    }
                    break; // End of Message Centre case

                case 5: // Logout
                    System.out.println("Logging out from messaging...");
                    messagingMenu = false; // Exit the messaging menu loop
                    break;

                default: // Handle invalid main menu choices
                    System.out.println("Invalid choice. Please select an option between 1 and 5.");
            }
        }
    }
}
