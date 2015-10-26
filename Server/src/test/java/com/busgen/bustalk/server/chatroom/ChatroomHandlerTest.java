package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by danie on 2015-10-13.
 */
public class ChatroomHandlerTest {

    private Session session;
    private ChatroomHandler chatroomHandler;
    private UserHandler userHandler;
    private User user;

    public ChatroomHandlerTest () {
        chatroomHandler = ChatroomHandler.getInstance();
        userHandler = UserHandler.getInstance();

        user = new User("username", "interests");
        user.setGroupId("testGroup");
        session = Mockito.mock(Session.class);

        userHandler.addUser(user, session);

        chatroomHandler.createChatroom(user, "chatroom 1"); // This will have chat id 100
    }

    @Test
    public void testIfUserCreatedChatroomsAreCreatedCorrectly() {
        Chatroom chatroom = chatroomHandler.createChatroom(user, "test chatroom");

        assertTrue(chatroomHandler.getListOfOpenChatrooms().contains(chatroom)
                && chatroomHandler.getGroupOfChatrooms(user.getGroupId()).contains(chatroom));
    }

    @Test
    public void testIfUserCreatedChatroomIsDeletedWhenLastUserLeaves() {
        Chatroom chatroom = chatroomHandler.createChatroom(user, "chat room 123");
        chatroomHandler.joinChatroom(user, chatroom);

        assertTrue(chatroom.getChatroomUsers().contains(user));

        chatroomHandler.leaveChatroom(user, chatroom);

        assertFalse(chatroomHandler.getListOfOpenChatrooms().contains(chatroom));
    }

    @Test
    public void testIfUserCreatedChatroomsHaveTheCorrectGroupId() {
        Chatroom chatroom = chatroomHandler.createChatroom(user, "test");

        assertTrue(chatroomHandler.getGroupOfChatrooms(user.getGroupId()).contains(chatroom));
    }
}