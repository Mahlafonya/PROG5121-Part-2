package com.project;

import com.project.Model.Message;
import com.project.Model.User;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * JUnit 5 test class for the Message handling and reporting features,
 * and also basic tests for Message class utility methods.
 */
public class MessageAppTests {

    private static final String TEST_FILE_NAME = "messages.json";
    private static List<User> testUsers;

    /**
     * This method runs once before all test methods in this class.
     * It sets up the initial test data by populating the `messages.json` file
     * and initializes test users.
     */
    @BeforeAll
    static void setupAll() {
        System.out.println("\n--- Setting up initial test data for all JUnit tests ---");
        File testFile = new File(TEST_FILE_NAME);
        if (testFile.exists()) {
            testFile.delete();
        }

        testUsers = new ArrayList<>();
        testUsers.add(new User("user1", "John", "Doe", "Password1@", "+27830000001"));
        testUsers.add(new User("user2", "Jane", "Doe", "Password2@", "+27830000002"));
        testUsers.add(new User("Developer", "Dev", "User", "DevPass1@", "0838884567"));

        Message testMsg1 = new Message("+27830000001", "+27830000001", "Did you get the cake?", "Sent");
        Message testMsg2 = new Message("0838884567","+27830000002", "Where are you? You are late! I have asked you to be on time.", "Stored");
        Message testMsg3 = new Message("0838884567","+27830000002", "Yohoooo, I am at your gate.", "Disregard");
        Message testMsgInvalid = new Message("+27830000001", "12345", "This is an invalid recipient cell", "Sent");

        Message.storeMessage(Arrays.asList(testMsg1, testMsg2, testMsg3, testMsgInvalid));

        Message.loadMessagesFromJson();
        System.out.println("Initial test data setup complete. " + Message.returnTotalMessages() + " messages loaded.");
    }

    /**
     * This method runs before each individual test method.
     * It reloads all messages from the JSON file into memory to ensure each test
     * starts with a consistent and fresh state, especially after tests that modify data (like deletion).
     */
    @BeforeEach
    void reloadDataBeforeEachTest() {
        Message.loadMessagesFromJson();
        System.out.println("\nReloaded data before test. Total messages: " + Message.returnTotalMessages());
    }

    /**
     * Test case for `checkMessageID()` method.
     */
    @Test
    @DisplayName("Test: Message ID generation and check")
    void testCheckMessageID() {
        User sender = testUsers.get(0);
        User receiver = testUsers.get(1);
        Message msg = new Message(sender.getPhoneNumber(), receiver.getPhoneNumber(), "Test message for ID", "Sent");
        String id = msg.getMessageID();
        assertNotNull(id, "Message ID should not be null.");
        assertTrue(id.length() <= 10, "Message ID length should be <=10.");
        assertTrue(msg.checkMessageID(), "checkMessageID() should return true for a valid ID.");
    }

    /**
     * Test case for `checkRecipientCell()` with a valid phone number.
     */
    @Test
    @DisplayName("Test: Recipient cell number validation (valid)")
    void testCheckRecipientCell_Valid() {
        User sender = testUsers.get(0);
        User receiver = testUsers.get(1);
        Message msg = new Message(sender.getPhoneNumber(), receiver.getPhoneNumber(), "Hello", "Sent");
        assertTrue(msg.checkRecipientCell(), "checkRecipientCell() should return true for a valid phone number.");
    }

    /**
     * Test case for `checkRecipientCell()` with an invalid phone number.
     */
    @Test
    @DisplayName("Test: Recipient cell number validation (invalid)")
    void testCheckRecipientCell_Invalid() {
        User sender = testUsers.get(0);
        User receiver = testUsers.get(2);
        Message msg1 = new Message(sender.getPhoneNumber(), "12345", "Hello", "Sent");
        assertFalse(msg1.checkRecipientCell(), "checkRecipientCell() should return false for an empty phone number.");

        Message msg3 = new Message(sender.getPhoneNumber(), receiver.getPhoneNumber()+"124", "Hello",  "Sent");
        assertFalse(msg3.checkRecipientCell(), "checkRecipientCell() should return false for a phone number that is too long.");
    }

    /**
     * Test case for `createMessageHash()` method.
     */
    @Test
    @DisplayName("Test: Message hash creation")
    void testCreateMessageHash() {
        User sender = testUsers.get(0);
        User receiver = testUsers.get(1);
        Message msg = new Message(sender.getPhoneNumber(), receiver.getPhoneNumber(), "Hello world", "Sent");
        String hash = msg.createMessageHash();
        assertNotNull(hash, "Message hash should not be null.");
        assertFalse(hash.isEmpty(), "Message hash should not be empty.");
        String[] parts = hash.split(":");
        assertEquals(3, parts.length, "Hash should have three parts separated by ':'");
        assertEquals(msg.getMessageID().substring(0, 2), parts[0], "First part should be first two digits of messageID");
        assertEquals(String.valueOf(msg.getMessageNumber()), parts[1], "Second part should be the message number");
        assertEquals("HELLOWORLD", parts[2], "Third part should be content in caps with no spaces");
    }

    /**
     * Test case for `returnTotalMessages()` method.
     * This relies on `loadMessagesFromJson` which loads all messages from the file.
     */
    @Test
    @DisplayName("Test: Total messages count")
    void testReturnTotalMessages() {
        assertEquals(4, Message.returnTotalMessages(),
                "Total messages loaded should be 4 as per initial test data.");
    }

    /**
     * Test case for `storeMessage()` method, verifying no exception is thrown.
     */
    @Test
    @DisplayName("Test: Storing a message does not throw an exception")
    void testStoreMessageDoesNotThrow() {
        User sender = testUsers.get(0);
        User receiver = testUsers.get(1);
        Message newMessage = new Message(sender.getPhoneNumber(), receiver.getPhoneNumber(), "New message for storing", "Sent");
        assertDoesNotThrow(() -> Message.storeMessage(List.of(newMessage)),
                "Storing a message should not throw any exceptions.");

        assertEquals(5, Message.returnTotalMessages(),
                "Total messages count should increase by 1 after adding a new message.");
    }

    /**
     * Test case for `printMessages()` method.
     */
    @Test
    @DisplayName("Test: Print messages format")
    void testPrintMessagesFormat() {
        User sender = testUsers.get(0);
        User receiver = testUsers.get(1);
        List<Message> testMessages = Arrays.asList(
            new Message(sender.getPhoneNumber(), receiver.getPhoneNumber(), "Msg1", "Sent"),
            new Message(receiver.getPhoneNumber(), sender.getPhoneNumber(), "Msg2", "Stored")
        );

        String printedOutput = Message.printMessages(testMessages);
        assertNotNull(printedOutput, "Printed output should not be null.");
        assertFalse(printedOutput.isEmpty(), "Printed output should not be empty.");
        assertTrue(printedOutput.contains("From: "+sender.getPhoneNumber() + " | To: "+ receiver.getPhoneNumber()+" | Message: \"Msg1\" | Status: Sent | ID: "),
                "Printed output should contain details of Msg1.");
        assertTrue(printedOutput.contains("From: "+receiver.getPhoneNumber() + " | To: "+ sender.getPhoneNumber()+" | Message: \"Msg2\" | Status: Stored | ID: "),
                "Printed output should contain details of Msg2.");
    }

    // --- Tests for Requirement 2.a: Display the sender and recipient of all sent messages. ---
    @Test
    @DisplayName("Test: Display all sent messages details")
    void testDisplaySentMessagesDetails() {
        String details = Message.displaySentMessagesDetails();
        assertNotNull(details, "Details string should not be null.");
        assertTrue(details.contains("--- All Sent Messages ---"), "Details should contain the header.");
        assertTrue(details.contains("Sender: +27830000001, Recipient: +27830000001, Message: \"Did you get the cake?\""),
                "Details should include the first sent message.");
        // Only testMsg1 is 'Sent' in the setup data
        assertFalse(details.contains("Where are you?"), "Details should not include stored messages.");
    }

    // --- Tests for Requirement 2.b: Display the longest sent message. ---
    @Test
    @DisplayName("Test: Display the longest message content")
    void testGetLongestMessageContent() {
        String longestMessageContent = Message.getLongestMessageContent();
        assertEquals("Where are you? You are late! I have asked you to be on time.", longestMessageContent,
                "The longest message content should match the expected one from all test data.");
    }

    // --- Tests for Requirement 2.c: Search for a message ID and display corresponding recipient and message. ---
    @Test
    @DisplayName("Test: Search for message by ID (valid ID)")
    void testSearchMessageById_Valid() {
        Message msg = Message.getMessages().stream()
                .filter(m -> m.getContent().equals("Did you get the cake?"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test setup error: Message not found."));
        String idToSearch = msg.getMessageID();

        String expectedOutput = "Message Found (ID: " + idToSearch + ")\n" +
                                "Recipient: +27830000001\n" +
                                "Message: \"Did you get the cake?\"";
        String actualOutput = Message.searchMessageById(idToSearch);
        assertEquals(expectedOutput, actualOutput, "Should find message by valid ID.");
    }

    @Test
    @DisplayName("Test: Search for message by ID (invalid ID)")
    void testSearchMessageById_Invalid() {
        String invalidId = "nonexistentID";
        String expectedOutput = "Message with ID '" + invalidId + "' not found.";
        String actualOutput = Message.searchMessageById(invalidId);
        assertEquals(expectedOutput, actualOutput, "Should return 'not found' for invalid ID.");
    }

    // --- Tests for Requirement 2.d: Search for all messages sent to a particular recipient. ---
    @Test
    @DisplayName("Test: Search messages by recipient (valid recipient, sent/stored)")
    void testSearchMessagesByRecipient_Valid() {
        String recipient = "+27830000002";
        String actualOutput = Message.searchMessagesByRecipient(recipient);

        assertTrue(actualOutput.contains("--- Messages for Recipient " + recipient + " ---"), "Output should contain header.");
        assertTrue(actualOutput.contains("Message: \"Where are you? You are late! I have asked you to be on time.\""), "Output should contain stored message.");
        assertFalse(actualOutput.contains("Message: \"Yohoooo, I am at your gate.\""), "Output should contain disregarded message (if included by your logic).");
        assertFalse(actualOutput.contains("Did you get the cake?"), "Output should not contain messages for other recipients.");
    }

    @Test
    @DisplayName("Test: Search messages by recipient (no messages found)")
    void testSearchMessagesByRecipient_NotFound() {
        String recipient = "+27999999999";
        String expectedOutput = "No sent or stored messages found for recipient '" + recipient + "'.";
        String actualOutput = Message.searchMessagesByRecipient(recipient);
        assertEquals(expectedOutput, actualOutput, "Should return 'not found' for non-existent recipient.");
    }

    // --- Tests for Requirement 2.e: Delete a message using the message hash. ---
    @Test
    @DisplayName("Test: Delete message by hash (valid hash)")
    void testDeleteMessageByHash_Valid() {
        Message msg2 = Message.getMessages().stream()
                .filter(m -> m.getContent().equals("Where are you? You are late! I have asked you to be on time."))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Test setup error: Test Message 2 not found."));
        String hashToDelete = msg2.createMessageHash();
        assertNotNull(hashToDelete, "Hash for message 2 should not be null.");

        int initialSize = Message.returnTotalMessages();

        String deletionResult = Message.deleteMessageByHash(hashToDelete);
        String expectedDeletionMessage = "Message \"Where are you? You are late! I have asked you to be on time.\" successfully deleted.";

        assertEquals(expectedDeletionMessage, deletionResult, "Deletion should return the correct success message.");
        assertEquals(initialSize - 1, Message.returnTotalMessages(), "Total message count should decrease by 1.");
        assertFalse(Message.getMessages().stream().anyMatch(m -> m.getMessageID().equals(msg2.getMessageID())),
                "Deleted message should no longer be in the in-memory list.");

        ObjectMapper mapper = new ObjectMapper();
        File file = new File(TEST_FILE_NAME);
        try {
            List<Message> messagesFromFile = Arrays.asList(mapper.readValue(file, Message[].class));
            assertFalse(messagesFromFile.stream().anyMatch(m -> m.getMessageID().equals(msg2.getMessageID())),
                    "Deleted message should not be found in the JSON file after deletion.");
        } catch (IOException e) {
            fail("Failed to read JSON file after deletion for verification: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test: Delete message by hash (invalid hash)")
    void testDeleteMessageByHash_Invalid() {
        int initialSize = Message.returnTotalMessages();
        String invalidHash = "invalidhash1234567890123456789012345678901234567890123456789012345";
        String expectedResult = "Message with hash '" + invalidHash + "' not found.";
        String actualResult = Message.deleteMessageByHash(invalidHash);

        assertEquals(expectedResult, actualResult, "Should return 'not found' for invalid hash.");
        assertEquals(initialSize, Message.returnTotalMessages(), "Total message count should not change for invalid hash.");
    }

    // --- Tests for Requirement 2.f: Display a report that lists the full details of all the sent messages. ---
    @Test
    @DisplayName("Test: Generate sent messages report")
    void testGenerateSentMessagesReport() {
        String report = Message.generateSentMessagesReport();

        assertNotNull(report, "Report should not be null.");
        assertTrue(report.contains("--- Sent Messages Report ---"), "Report should contain the correct header.");

        assertTrue(report.contains("Message: \"Did you get the cake?\""), "Report should include content of Test Message 1.");
        assertTrue(report.contains("Sender: +27830000001"), "Report should include sender's phone number of Test Message 1.");

        assertFalse(report.contains("Where are you?"), "Report should not include stored messages.");
        assertFalse(report.contains("Yohoooo"), "Report should not include disregarded messages.");

        for (Message msg : Message.getSentMessages()) {
            assertTrue(report.contains("Message Hash: " + msg.createMessageHash()), "Report should contain hash for " + msg.getContent());
            assertTrue(report.contains("Message ID: " + msg.getMessageID()), "Report should contain ID for " + msg.getContent());
        }
    }
}
