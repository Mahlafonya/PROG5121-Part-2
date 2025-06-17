package com.project.Model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
    private static final String FILE_NAME = "messages.json";

    private static List<Message> allMessagesInMemory = new ArrayList<>();
    private static List<Message> sentMessages = new ArrayList<>();
    private static List<Message> disregardedMessages = new ArrayList<>();
    private static List<Message> storedMessages = new ArrayList<>();
    private static List<String> messageHashes = new ArrayList<>();
    private static List<String> messageIDs = new ArrayList<>();

    private String messageID;
    private String sender;     
    private String recipient;
    private String content;
    private String flag;
    private int MessageNumber;
    private String hash;

    public Message() {}

    public Message(String senderCell, String recipientCell, String content, String flag) {
        this.sender = senderCell;
        this.recipient = recipientCell;
        this.content = content;

        Random random = new Random();
        long min = 1_000_000_000L;
        long max = 9_999_999_999L;
        this.messageID = String.valueOf(min + (long)(random.nextDouble() * (max - min + 1)));

        this.flag = flag;
        this.hash = createMessageHash();
    }

    public String getMessageID() { return messageID; }
    public void setMessageID(String messageID) { this.messageID = messageID; }

    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }

    public int getMessageNumber() { return MessageNumber; }
    public void setMessageNumber(int MessageNumber) { this.MessageNumber = MessageNumber; }

    public String getHash() {
        // Always generate the hash dynamically to keep it up to date
        return createMessageHash();
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public boolean checkMessageID() {
        return messageID != null && messageID.matches("\\d{10}");
    }

    public boolean checkRecipientCell() {
        return recipient != null && (recipient.matches("^\\+27\\d{9}$") || recipient.matches("^0[6-8]\\d{8}$"));
    }

    /**
     * Autogenerates a Message Hash in the format:
     * [first two digits of message ID]:[message sequence number]:[first word][last word of message content]
     * The hash is displayed in all caps.
     * Example: "00:0:HITHANKS"
     * @return The custom formatted message hash string.
     */
    public String createMessageHash() {
        String firstTwo = (messageID != null && messageID.length() >= 2) ? messageID.substring(0, 2) : "XX";
        String msgNum = String.valueOf(this.MessageNumber);
        String contentPart = (content != null) ? content.replaceAll("\\s+", "").toUpperCase() : "";
        return firstTwo + ":" + msgNum + ":" + contentPart;
    }

    public String sentMessage(String option) {
        switch (option.toLowerCase()) {
            case "send":
                this.setFlag("Sent");
                return "Message sent.";
            case "store":
                this.setFlag("Stored");
                return "Message stored.";
            case "discard":
                this.setFlag("Disregard");
                return "Message discarded.";
            default:
                return "Invalid option. Message status not set.";
        }
    }

    public static String printMessages(List<Message> messages) {
        StringBuilder sb = new StringBuilder();
        if (messages == null || messages.isEmpty()) {
            return "No messages to display.";
        }
        for (Message m : messages) {
            sb.append("From: ").append(m.getSender())
              .append(" | To: ").append(m.getRecipient())
              .append(" | Message: \"").append(m.getContent()).append("\"")
              .append(" | Status: ").append(m.getFlag())
              .append(" | ID: ").append(m.getMessageID());
            String hash = m.createMessageHash();
            if (hash != null) {
                sb.append(" | Hash: ").append(hash);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return allMessagesInMemory.size();
    }

    public static void loadMessagesFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_NAME);

        allMessagesInMemory.clear();
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();

        if (file.exists()) {
            try {
                Message[] existingMessagesArray = mapper.readValue(file, Message[].class);
                for (int i = 0; i < existingMessagesArray.length; i++) {
                    Message msg = existingMessagesArray[i];
                    msg.setMessageNumber(i); // Assign MessageNumber based on order in file
                    msg.setHash(msg.createMessageHash()); // Ensure hash is set/updated
                    allMessagesInMemory.add(msg);

                    messageIDs.add(msg.getMessageID());

                    String hash = msg.getHash();
                    if (hash != null) {
                        messageHashes.add(hash);
                    }

                    switch (msg.getFlag().toLowerCase()) {
                        case "sent":
                            sentMessages.add(msg);
                            break;
                        case "stored":
                            storedMessages.add(msg);
                            break;
                        case "disregard":
                            disregardedMessages.add(msg);
                            break;
                    }
                }
                System.out.println("Messages loaded successfully from " + FILE_NAME + ". Total: " + allMessagesInMemory.size());
            } catch (IOException e) {
                System.err.println("Error reading existing messages from " + FILE_NAME + ": " + e.getMessage());
            }
        } else {
            System.out.println("No existing messages file found (" + FILE_NAME + "). Starting with empty message lists.");
        }
    }

    public static void storeMessage(List<Message> newMessages) {
        ObjectMapper mapper = new ObjectMapper();

        for (Message msg : newMessages) {
            msg.setMessageNumber(allMessagesInMemory.size()); // Current size is its new index
            msg.setHash(msg.createMessageHash()); // Ensure hash is set/updated
            allMessagesInMemory.add(msg);

            messageIDs.add(msg.getMessageID());
            String hash = msg.getHash();
            if (hash != null) {
                messageHashes.add(hash);
            }

            switch (msg.getFlag().toLowerCase()) {
                case "sent":
                    sentMessages.add(msg);
                    break;
                case "stored":
                    storedMessages.add(msg);
                    break;
                case "disregard":
                    disregardedMessages.add(msg);
                    break;
            }
        }

        File file = new File(FILE_NAME);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, allMessagesInMemory);
            System.out.println("Messages saved to " + FILE_NAME + ". Total messages in file: " + allMessagesInMemory.size());
        } catch (IOException e) {
            System.err.println("Error saving messages to " + FILE_NAME + ": " + e.getMessage());
        }
    }

    public static List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        return new ArrayList<>(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    public static List<Message> getMessages() {
        return new ArrayList<>(allMessagesInMemory);
    }

    public static String displaySentMessagesDetails() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to display.";
        }
        StringBuilder sb = new StringBuilder("\n--- All Sent Messages ---\n");
        for (Message m : sentMessages) {
            sb.append("Sender: ").append(m.getSender())
              .append(", Recipient: ").append(m.getRecipient())
              .append(", Message: \"").append(m.getContent()).append("\"\n");
        }
        return sb.toString();
    }

    public static String getLongestMessageContent() {
        if (allMessagesInMemory.isEmpty()) {
            return "No messages available to determine the longest.";
        }
        Message longest = null;
        for (Message m : allMessagesInMemory) {
            if (m.getContent() != null && (longest == null || m.getContent().length() > longest.getContent().length())) {
                longest = m;
            }
        }
        return longest != null ? longest.getContent() : "No messages with content found.";
    }

    public static String searchMessageById(String messageId) {
        if (messageId == null || messageId.trim().isEmpty()) {
            return "Message ID cannot be empty.";
        }
        for (Message m : allMessagesInMemory) {
            if (m.getMessageID().equals(messageId.trim())) {
                return "Message Found (ID: " + messageId + ")\n" +
                       "Recipient: " + m.getRecipient() + "\n" +
                       "Message: \"" + m.getContent() + "\"";
            }
        }
        return "Message with ID '" + messageId + "' not found.";
    }

    public static String searchMessagesByRecipient(String recipientPhoneNumber) {
        if (recipientPhoneNumber == null || recipientPhoneNumber.trim().isEmpty()) {
            return "Recipient phone number cannot be empty.";
        }
        List<Message> found = allMessagesInMemory.stream()
            .filter(m -> m.getRecipient().equals(recipientPhoneNumber.trim()) &&
                         (m.getFlag().equalsIgnoreCase("Sent") || m.getFlag().equalsIgnoreCase("Stored")))
            .collect(Collectors.toList());

        if (found.isEmpty()) {
            return "No sent or stored messages found for recipient '" + recipientPhoneNumber + "'.";
        }

        StringBuilder sb = new StringBuilder("\n--- Messages for Recipient " + recipientPhoneNumber + " ---\n");
        for (Message m : found) {
            sb.append("Status: ").append(m.getFlag())
              .append(", Message: \"").append(m.getContent()).append("\"\n");
        }
        return sb.toString();
    }

    public static String deleteMessageByHash(String messageHash) {
        if (messageHash == null || messageHash.trim().isEmpty()) {
            return "Message hash cannot be empty.";
        }

        Message messageToDelete = null;
        String actualHashOfFoundMessage = null;

        Iterator<Message> iterator = allMessagesInMemory.iterator();
        while (iterator.hasNext()) {
            Message m = iterator.next();
            String currentMessageHash = m.createMessageHash();
            if (currentMessageHash != null && currentMessageHash.equals(messageHash.trim())) {
                messageToDelete = m;
                actualHashOfFoundMessage = currentMessageHash;
                iterator.remove();
                break;
            }
        }

        if (messageToDelete != null) {
            sentMessages.remove(messageToDelete);
            disregardedMessages.remove(messageToDelete);
            storedMessages.remove(messageToDelete);
            messageIDs.remove(messageToDelete.getMessageID());
            if (actualHashOfFoundMessage != null) {
                messageHashes.remove(actualHashOfFoundMessage);
            }

            ObjectMapper mapper = new ObjectMapper();
            File file = new File(FILE_NAME);
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, allMessagesInMemory);
                System.out.println("Message successfully deleted from " + FILE_NAME);
                return "Message \"" + messageToDelete.getContent() + "\" successfully deleted.";
            } catch (IOException e) {
                System.err.println("Error rewriting JSON after deletion: " + e.getMessage());
                return "Error deleting message from file: " + e.getMessage();
            }
        } else {
            return "Message with hash '" + messageHash + "' not found.";
        }
    }

    public static String generateSentMessagesReport() {
        if (sentMessages.isEmpty()) {
            return "No sent messages to generate a report.";
        }
        StringBuilder sb = new StringBuilder("\n--- Sent Messages Report ---\n");
        for (Message m : sentMessages) {
            sb.append("Message Hash: ").append(m.createMessageHash()).append("\n")
              .append("Message ID: ").append(m.getMessageID()).append("\n")
              .append("Sender: ").append(m.getSender()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: \"").append(m.getContent()).append("\"\n")
              .append("----------------------------\n");
        }
        return sb.toString();
    }
}
