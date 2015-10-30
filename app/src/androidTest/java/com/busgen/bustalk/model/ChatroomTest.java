package com.busgen.bustalk.model;

import junit.framework.TestCase;

/**
 * Created by nalex on 30/10/2015.
 */
public class ChatroomTest extends TestCase{
    protected Chatroom chatroom;
    protected User user;
    protected int chatId;

    @Override
    public void setUp(){
        chatId = 0;
        String chatName = "test";
        chatroom = new Chatroom(chatId, chatName);
        user = new User();
        user.setUserName("test");
    }

    public void testAddUser(){
        chatroom.addUser(user);
        assertTrue(chatroom.containsUser(user));
    }

    public void testDoesNotContainUser(){
        User user2 = new User();
        chatroom.addUser(user2);
        assertFalse(chatroom.containsUser(user));
    }

    public void testRemoveUser(){
        chatroom.addUser(user);
        chatroom.removeUser(user);
        assertFalse(chatroom.containsUser(user));
    }

    public void testChatroomIsEmpty(){
        assertTrue(chatroom.isEmpty());
    }

    public void testChatroomIsNotEmpty(){
        chatroom.addUser(user);
        assertFalse(chatroom.isEmpty());
    }

    public void testEqualsWithSelf(){
        assertTrue(chatroom.equals(chatroom));
    }

    public void testEqualsWithNull(){
        assertFalse(chatroom.equals(null));
    }

    public void testEqualsIfEqual(){
        Chatroom otherChatroom = new Chatroom(chatId, "otherRoom");
        assertTrue(chatroom.equals(otherChatroom));
    }

    public void testEqualsIfEqualIfNotEqual(){
        Chatroom otherChatroom = new Chatroom(chatId+1, "otherRoom");
        assertFalse(chatroom.equals(otherChatroom));
    }

    public void testHascodeIfEqual(){
        Chatroom otherChatroom = new Chatroom(chatId, "otherRoom");
        assertTrue(otherChatroom.hashCode() == chatroom.hashCode());
    }

    public void testHascodeIfNotEqual(){
        Chatroom otherChatroom = new Chatroom(chatId+1, "otherRoom");
        assertFalse(chatroom.hashCode() == otherChatroom.hashCode());
    }
}
