/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.project;

import com.project.Model.Message;
import com.project.Model.User;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * JUnit Tests for Message and RunMessaging class methods.
 */
public class TestMessageAndRunMessaging {

    private List<Message> messages;
    private List<User> users;
    private User sender;
    private User receiver;

    @Before
    public void setUp() {
        messages = new ArrayList<>();
        users = new ArrayList<>();

        sender = new User("user1", "John", "Doe", "Password1@", "+27830000001");
        receiver = new User("user2", "Jane", "Doe", "Password2@", "+27830000002");

        users.add(sender);
        users.add(receiver);
    }

    @Test
    public void testCheckMessageID() {
        Message msg = new Message(sender.getUsername(), receiver.getUsername(), "Hello", sender.getPhoneNumber());
        String id = msg.getMessageID();
        assertNotNull(id);
        assertTrue(id.matches("[a-zA-Z0-9]+"));
    }


    @Test
    public void testCheckRecipientCell_Invalid() {
        Message msg = new Message(sender.getUsername(), receiver.getUsername(), "Hello", "");
        assertFalse(msg.checkRecipientCell());
    }

    @Test
    public void testCreateMessageHash() {
        Message msg = new Message(sender.getUsername(), receiver.getUsername(), "Hello", sender.getPhoneNumber());
        String hash = msg.createMessageHash();
        assertNotNull(hash);
        assertFalse(hash.isEmpty());
    }

    @Test
    public void testReturnTotalMessages() {
        Message msg1 = new Message(sender.getUsername(), receiver.getUsername(), "Hello", sender.getPhoneNumber());
        Message msg2= new Message(sender.getUsername(), receiver.getUsername(), "What's up?", sender.getPhoneNumber());

        messages.add(msg1);
        messages.add(msg2);
        assertEquals(2, Message.returnTotalMessages());
    }

    @Test
    public void testStoreMessageDoesNotThrow() {
        messages.add(new Message("user1", "user2", "Hello", "+27123456789"));
        try {
            Message.storeMessage(messages);
        } catch (Exception e) {
            fail("Exception thrown: " + e.getMessage());
        }
    }


    @Test
    public void testPrintMessagesForReceiver() {
        messages.add(new Message("user1", "user2", "Hello", "+27123456789"));
        messages.add(new Message("user3", "user2", "Another","+27234567890"));
        messages.add(new Message("user2", "user1", "Reply","+27123459876"));

        System.out.println("Messages for user2:");
        Message.printMessages(messages);
        // No assert, since we’re printing, but we make sure no exception is thrown
    }
}
