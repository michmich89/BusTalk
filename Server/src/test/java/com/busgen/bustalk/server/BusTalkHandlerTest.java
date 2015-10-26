package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import java.util.List;

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

        Chatroom first = chatroomHandler.getChatroom(10000);
        Chatroom second = chatroomHandler.getChatroom(10001);
        Chatroom third = chatroomHandler.getChatroom(10002);

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

    @Test
    public void testIfUserLeavesChatroomsWhenChangingGroup() {
        Session userSession = Mockito.mock(Session.class);
        Mockito.when(userSession.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));
        // Create user
        userHandler.setUserNameAndInterests(null, userSession, "abc123", "interests");
        User user = userHandler.getUser(userSession);
        // Change group and join main chat
        busTalkHandler.handleInput(changeGroup("1"), userSession);
        Chatroom chatroom1 = chatroomHandler.getGroupOfChatrooms("1").get(0);
        busTalkHandler.handleInput(joinRoom(chatroom1.getIdNbr()), userSession);

        // Check if user is in the room, then change group and check once again if the user is in the room
        assertTrue(chatroom1.getChatroomUsers().contains(user));
        busTalkHandler.handleInput(changeGroup("2"), userSession);
        assertFalse(chatroom1.getChatroomUsers().contains(user));
    }

    @Test
    public void testIfMainChatroomsAreCreatedWhenAUserJoinsAGroupWithNoChatrooms() {
        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "user001", "interests");

        String groupId = "group";
        busTalkHandler.handleInput(changeGroup(groupId), userSession);
        List<Chatroom> chatrooms = chatroomHandler.getGroupOfChatrooms(groupId);

        assertTrue(chatrooms != null && chatrooms.size() == 1);
    }

    @Test
    public void testIfGroupAndItsMainChatroomAreRemovedWhenLastUserLeavesAGroup() {
        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "testing123", "interests");

        // Join a group and get the list of chat rooms in that group (should only be one)
        String groupId1 = "group1";
        busTalkHandler.handleInput(changeGroup(groupId1), userSession);
        List<Chatroom> chatrooms1 = chatroomHandler.getGroupOfChatrooms(groupId1);
        // Join another group and get the list of the same group as before
        String groupId2 = "group2";
        busTalkHandler.handleInput(changeGroup(groupId2), userSession);
        List<Chatroom> chatrooms2 = chatroomHandler.getGroupOfChatrooms(groupId1);
        // Check if the first list is empty, and if the group was removed (so that it returns null after user has
        // changed group)
        assertTrue(chatrooms1.size() == 0 && chatrooms2 == null);
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