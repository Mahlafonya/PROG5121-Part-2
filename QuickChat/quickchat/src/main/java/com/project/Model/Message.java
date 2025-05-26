package com.project.Model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
    private static final String FILE_NAME = "messages.json";
    private static int messageCounter = 0;

    private String messageID;
    private String sender;
    private String receiver;
    private String content;
    private String recipientCell;

    public Message() {}

    public Message(String sender, String receiver, String content, String cellNumber) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.messageID = UUID.randomUUID().toString().substring(0, 8);
        this.recipientCell = cellNumber;
        messageCounter++;
    }


    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public boolean checkRecipientCell() {
        if (recipientCell == null || recipientCell.length() > 10) return false;
        if (!recipientCell.matches("^0\\d{9}$")) return false; 
        return true;
    }

    public String createMessageHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = sender + receiver + content;
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch (Exception e) {
            return null;
        }
    }

    public String sentMessage(String option) {
        switch (option.toLowerCase()) {
            case "send":
                return "Message sent.";
            case "store":
                storeMessage(Collections.singletonList(this));
                return "Message stored.";
            case "discard":
                return "Message discarded.";
            default:
                return "Invalid option.";
        }
    }

    public static String printMessages(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        for (Message m : messages) {
            sb.append("From: ").append(m.getSender())
              .append(" | To: ").append(m.getReceiver())
              .append(" | Message: ").append(m.getContent()).append("\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return messageCounter;
    }

public static void storeMessage(List<Message> newMessages) {
    ObjectMapper mapper = new ObjectMapper();
    List<Message> allMessages = new ArrayList<>();

    File file = new File(FILE_NAME);
    if (file.exists()) {
        try {
            Message[] existingMessages = mapper.readValue(file, Message[].class);
            allMessages.addAll(Arrays.asList(existingMessages));
        } catch (IOException e) {
            System.out.println("Error reading existing messages: " + e.getMessage());
        }
    }

    allMessages.addAll(newMessages);

    try {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, allMessages);
    } catch (IOException e) {
        System.out.println("Error saving messages: " + e.getMessage());
    }
}


    public String getMessageID() { return messageID; }
    public void setMessageID(String messageID) { this.messageID = messageID; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getRecipientCell() { return recipientCell; }
    public void setRecipientCell(String recipientCell) { this.recipientCell = recipientCell; }
}
