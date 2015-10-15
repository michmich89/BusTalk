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

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        Mockito.when(session.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));
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

        //Create second simulated userMessage
        JSONObject messageTwo = new JSONObject();
        messageTwo.put("type", MessageType.CHANGE_GROUP_ID);
        messageTwo.put("groupId", "secondGroup");
        UserMessage userMessageTwo = new UserMessage(messageTwo);

        //Send second message
        busTalkHandler.handleInput(userMessageTwo, session);

        //Save second ID
        String secondGroupId = user.getGroupId();

        assertTrue(firstGroupId != null && secondGroupId != null && !firstGroupId.equals(secondGroupId));
    }

    @Test
    public void UserLeavingAllRoomsWhenSessionIsClosed(){
        User user = new User("username", "userinterests");
        Session session = Mockito.mock(Session.class);
        userHandler.addUser(user, session);
        Mockito.when(session.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));

        //Add group to user
        JSONObject message = new JSONObject();
        message.put("type", MessageType.CHANGE_GROUP_ID);
        message.put("groupId", "firstGroup");
        UserMessage userMessage = new UserMessage(message);

        busTalkHandler.handleInput(userMessage, session);

        //Create simulated userMessages
        JSONObject join1 = new JSONObject();
        join1.put("type", MessageType.CREATE_ROOM_REQUEST);
        join1.put("chatName", "firstRoom");
        UserMessage userMessage1 = new UserMessage(join1);

        JSONObject join2 = new JSONObject();
        join2.put("type", MessageType.CREATE_ROOM_REQUEST);
        join2.put("chatName", "secondRoom");
        UserMessage userMessage2 = new UserMessage(join2);

        JSONObject join3 = new JSONObject();
        join3.put("type", MessageType.CREATE_ROOM_REQUEST);
        join3.put("chatName", "thirdRoom");
        UserMessage userMessage3 = new UserMessage(join3);

        busTalkHandler.handleInput(userMessage1, session);
        busTalkHandler.handleInput(userMessage2, session);
        busTalkHandler.handleInput(userMessage3, session);

        Chatroom first = chatroomHandler.getChatroom(100);
        Chatroom second = chatroomHandler.getChatroom(101);
        Chatroom third = chatroomHandler.getChatroom(102);

        int nbr1 = first.getChatroomUsers().size();
        int nbr2 = second.getChatroomUsers().size();
        int nbr3 = third.getChatroomUsers().size();

        busTalkHandler.removeSession(session);

        int nbr1after = first.getChatroomUsers().size();
        int nbr2after = second.getChatroomUsers().size();
        int nbr3after = third.getChatroomUsers().size();

        System.out.println(nbr1);
        System.out.println(nbr1after);
        System.out.println(nbr2);
        System.out.println(nbr2after);
        System.out.println(nbr3);
        System.out.println(nbr3after);

        assertTrue(nbr1 == nbr2 && nbr2 == nbr3 && nbr1 == 1 && nbr1after == nbr2after && nbr2after == nbr3after && nbr1after == 0);


    }

    public void testIfUserLeavesChatroomsWhenChangingGroup() {
        Session userSession = Mockito.mock(Session.class);
        Mockito.when(userSession.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));
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