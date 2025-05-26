package com.project;

import java.util.List;
import java.util.Scanner;

import com.project.Model.Message;
import com.project.Model.User;

public class RunMessaging {
    public static void startMessaging(User loggedUser, List<User> users, List<Message> messages, Scanner sc) {

        boolean messagingMenu = true;
        while (messagingMenu) {
            System.out.println("\nChoose an option: 1 - Send Message, 2 - View Messages, 3 - View Stats, 4 - Logout");
            int msgChoice = sc.nextInt();
            sc.nextLine();

            switch (msgChoice) {
                case 1:
                    System.out.print("Enter recipient username: ");
                    String recipient = sc.nextLine();

                    User recipientUser = null;
                    for (User u : users) {
                        if (u.getUsername().equals(recipient)) {
                            recipientUser = u;
                            break;
                        }
                    }

                    if (recipientUser != null) {
                        System.out.print("Enter message: ");
                        String msgText = sc.nextLine();
                        String recipientCell = recipientUser.getPhoneNumber();

                        Message msg = new Message(loggedUser.getUsername(), recipient, msgText, recipientCell);

                        if (!msg.checkMessageID()) {
                            System.out.println("Error: Generated message ID is too long.");
                            break;
                        }

                        System.out.println("Choose what to do with this message (send/store/discard): ");
                        String decision = sc.nextLine();

                        String status = msg.sentMessage(decision);
                        System.out.println(status);

                        if (decision.equalsIgnoreCase("send")) {
                            messages.add(msg);
                            Message.storeMessage(List.of(msg));
                        } else if (decision.equalsIgnoreCase("store")) {
                            Message.storeMessage(List.of(msg));
                        }

                        String hash = msg.createMessageHash();
                        System.out.println("Message hash: " + hash);

                    } else {
                        System.out.println("Recipient not found.");
                    }
                    break;

                case 2:
                    System.out.println("Your messages:\n");
                    List<Message> userMessages = messages.stream()
                        .filter(m -> m.getReceiver().equals(loggedUser.getUsername()))
                        .toList();

                    System.out.println(Message.printMessages(userMessages));
                    break;

                case 3:
                    System.out.println("Total messages created: " + Message.returnTotalMessages());
                    break;

                case 4:
                    System.out.println("Logging out...");
                    messagingMenu = false;
                    break;

                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
