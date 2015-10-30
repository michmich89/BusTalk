package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.BusTalkHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;
import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import static org.junit.Assert.assertTrue;

public class GroupHandlerTest {

    private GroupHandler groupHandler;
    private UserHandler userHandler;
    private BusTalkHandler busTalkHandler;

    public GroupHandlerTest() {
        this.userHandler = UserHandler.getInstance();
        this.busTalkHandler = BusTalkHandler.getInstance();
        this.groupHandler = busTalkHandler.getGroupHandler();
    }

    @Test
    public void testIfUserCreatedChatroomIsRemovedWhenAUserLeavesRoom() {
        Session userSession1 = Mockito.mock(Session.class);
        Mockito.when(userSession1.getAsyncRemote()).thenReturn(Mockito.mock(RemoteEndpoint.Async.class));
        userHandler.setUserNameAndInterests(null, userSession1, "asd123", "interests");
        busTalkHandler.handleInput(changeGroup("room123"), userSession1);
        busTalkHandler.handleInput(createRoom("roooooom"), userSession1);

        assertTrue(groupHandler.getGroupRooms("room123").size() == 2);

        busTalkHandler.handleInput(leaveRoom(groupHandler.getGroupRooms("room123").get(1).getIdNbr()), userSession1);

        assertTrue(groupHandler.getGroupRooms("room123").size() == 1);
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

    private UserMessage leaveRoom(int room) {
        JSONObject json = new JSONObject();
        json.put("type", MessageType.LEAVE_ROOM_REQUEST);
        json.put("chatId", room);
        return new UserMessage(json);
    }

    private UserMessage createRoom(String title) {
        JSONObject json = new JSONObject();
        json.put("type", MessageType.CREATE_ROOM_REQUEST);
        json.put("chatName", title);
        return new UserMessage(json);
    }
}