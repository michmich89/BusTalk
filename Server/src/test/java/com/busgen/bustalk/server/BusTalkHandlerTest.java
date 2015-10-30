package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.group.GroupHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.IUser;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import com.busgen.bustalk.server.util.Constants;
import org.json.JSONObject;
import org.junit.Test;
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

    private UserHandler userHandler;
    private GroupHandler groupHandler;
    private BusTalkHandler busTalkHandler;

    public BusTalkHandlerTest(){
        busTalkHandler = BusTalkHandler.getInstance();
        userHandler = busTalkHandler.getUserHandler();
        groupHandler = busTalkHandler.getGroupHandler();
    }

    @Test
    public void TestChangeGroupIdOfUser(){
        //Create objects needed
        IUser user = new User("username", "userinterests");
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
        String firstGroupId = groupHandler.getGroupIdByUser(user);

        //Create second simulated userMessage
        JSONObject messageTwo = new JSONObject();
        messageTwo.put("type", MessageType.CHANGE_GROUP_ID);
        messageTwo.put("groupId", "secondGroup");
        UserMessage userMessageTwo = new UserMessage(messageTwo);

        //Send second message
        busTalkHandler.handleInput(userMessageTwo, session);

        //Save second ID
        String secondGroupId = groupHandler.getGroupIdByUser(user);

        assertTrue(firstGroupId != null && secondGroupId != null && !firstGroupId.equals(secondGroupId));
    }

    @Test
    public void UserLeavingAllRoomsWhenSessionIsClosed(){
        IUser user = new User("username", "userinterests");
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

        List<IUser> list1 = groupHandler.getUsersInRoom(Constants.NBR_OF_RESERVED_CHAT_IDS);
        List<IUser> list2 = groupHandler.getUsersInRoom(Constants.NBR_OF_RESERVED_CHAT_IDS + 1);
        List<IUser> list3 = groupHandler.getUsersInRoom(Constants.NBR_OF_RESERVED_CHAT_IDS + 2);

        int nbr1 = list1.size();
        int nbr2 = list2.size();
        int nbr3 = list3.size();

        busTalkHandler.removeSession(session);

        int nbr1after = list1.size();
        int nbr2after = list2.size();
        int nbr3after = list3.size();

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
        Session userSession1 = Mockito.mock(Session.class);
        Mockito.when(userSession1.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));
        Session userSession2 = Mockito.mock(Session.class);
        Mockito.when(userSession2.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));
        // Create users
        userHandler.setUserNameAndInterests(null, userSession1, "abc123", "interests");
        IUser user1 = userHandler.getUser(userSession1);
        userHandler.setUserNameAndInterests(null, userSession2, "123abc", "interests");
        IUser user2 = userHandler.getUser(userSession2);
        // Change group and join main chat
        busTalkHandler.handleInput(changeGroup("GROUPPPPPP"), userSession1);
        busTalkHandler.handleInput(changeGroup("GROUPPPPPP"), userSession2);
        int chatId = groupHandler.getGroupRooms("GROUPPPPPP").get(0).getIdNbr();
        busTalkHandler.handleInput(joinRoom(chatId), userSession1);
        busTalkHandler.handleInput(joinRoom(chatId), userSession2);

        // Check if user is in the room, then change group and check once again if the user is in the room
        assertTrue(groupHandler.getUsersInRoom(chatId).contains(user1));
        busTalkHandler.handleInput(changeGroup("2"), userSession1);
        assertFalse(groupHandler.getUsersInRoom(chatId).contains(user1));
    }

    @Test
    public void testIfMainChatroomsAreCreatedWhenAUserJoinsAGroupWithNoChatrooms() {
        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "user001", "interests");

        String groupId = "group";
        busTalkHandler.handleInput(changeGroup(groupId), userSession);
        List<IChatroom> chatrooms = groupHandler.getGroupRooms(groupId);

        assertTrue(chatrooms != null && chatrooms.size() == 1);
    }

    @Test
    public void testIfGroupIsRemovedWhenLastUserLeaves() {
        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "testing123", "interests");

        // Join a group and get the list of chat rooms in that group (should only be one)
        String groupId1 = "group1";
        busTalkHandler.handleInput(changeGroup(groupId1), userSession);
        // Join another group and get the list of the same group as before
        String groupId2 = "group2";
        busTalkHandler.handleInput(changeGroup(groupId2), userSession);
        // Check if the first list is empty, and if the group was removed (so that it returns null after user has
        // changed group)
        assertTrue(groupHandler.getGroupById("group1") == null && groupHandler.getGroupById("group2") != null);
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