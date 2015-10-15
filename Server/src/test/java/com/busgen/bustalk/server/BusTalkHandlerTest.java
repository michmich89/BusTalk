package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;
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
 * Created by danie on 2015-10-15.
 */
public class BusTalkHandlerTest {

    private ChatroomHandler chatroomHandler;
    private UserHandler userHandler;
    private BusTalkHandler busTalkHandler;
    private User user;

    public BusTalkHandlerTest(){
        chatroomHandler = ChatroomHandler.getInstance();
        userHandler = UserHandler.getInstance();
        busTalkHandler = BusTalkHandler.getInstance();
    }

    @Test
    public void TestChangeGroupIdOfUser(){
        //Create objects needed
        User user = new User("username", "userinterests");
        Session session = Mockito.mock(Session.class);
        userHandler.addUser(user, session);

        //Create first simulated userMessage
        JSONObject message = new JSONObject();
        message.put("type", MessageType.CHANGE_GROUP_ID);
        message.put("groupId", "firstGroup");
        UserMessage userMessage = new UserMessage(message);

        //Send first message
        busTalkHandler.handleInput(userMessage, session);

        //Save ID
        String firstGroupId = user.getGroupId();
        System.out.println(firstGroupId);

        //Create second simulated userMessage
        JSONObject messageTwo = new JSONObject();
        messageTwo.put("type", MessageType.CHANGE_GROUP_ID);
        messageTwo.put("groupId", "secondGroup");
        UserMessage userMessageTwo = new UserMessage(messageTwo);

        //Send second message
        busTalkHandler.handleInput(userMessageTwo, session);

        //Save second ID
        String secondGroupId = user.getGroupId();
        System.out.println(secondGroupId);

        assertTrue(firstGroupId != null && secondGroupId != null && !firstGroupId.equals(secondGroupId));
    }

    @Test
    public void testIfUserLeavesChatroomsWhenChangingGroup() {
        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "abc123", "interests");

        int chatId = 0;
        User user = userHandler.getUser(userSession);
        busTalkHandler.handleInput(changeGroup("1"), userSession);
        busTalkHandler.handleInput(joinRoom(chatId), userSession);
        Chatroom chatroom = chatroomHandler.getChatroom(chatId);
        assertTrue(chatroom.getChatroomUsers().contains(user));
        busTalkHandler.handleInput(changeGroup("2"), userSession);
        assertFalse(chatroom.getChatroomUsers().contains(user));
    }

    private UserMessage changeGroup(String group) {
        JSONObject json = new JSONObject();
        json.put("type", MessageType.CHANGE_GROUP_ID);
        json.put("groupId", group);
        return new UserMessage(json);
    }

    private UserMessage joinRoom(int room) {
        JSONObject json = new JSONObject();
        json.put("type", MessageType.JOIN_ROOM_REQUEST);
        json.put("chatId", room);
        return new UserMessage(json);
    }
}