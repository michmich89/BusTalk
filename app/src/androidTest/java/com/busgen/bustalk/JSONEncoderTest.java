package com.busgen.bustalk;

import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRooms;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgSetGroupId;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChat;
import com.busgen.bustalk.service.JSONEncoder;

import junit.framework.TestCase;

import org.json.JSONException;

/**
 * Created by danie on 2015-10-30.
 */
public class JSONEncoderTest extends TestCase {

    private JSONEncoder encoder;

    @Override
    public void setUp() {
        encoder = new JSONEncoder();
    }

    public void testIfChatMessageIsEncoded() {
        IServerMessage chatMessage = new MsgChatMessage(true, "", "", "", 0);
        try {
            encoder.encode(chatMessage);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfJoinRoomMessageIsEncoded() {
        IServerMessage joinRoom = new MsgJoinRoom(new Chatroom(0, ""));
        try {
            encoder.encode(joinRoom);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfCreateRoomMessageIsEncoded() {
        IServerMessage createRoom = new MsgCreateRoom(0, "");
        try {
            encoder.encode(createRoom);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfLeaveRoomMessageIsEncoded() {
        IServerMessage leaveRoom = new MsgLeaveRoom(0);
        try {
            encoder.encode(leaveRoom);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfChooseNicknameMessageIsEncoded() {
        IServerMessage nickName = new MsgChooseNickname("", "");
        try {
            encoder.encode(nickName);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfAvailableRoomsMessageIsEncoded() {
        IServerMessage availableRooms = new MsgAvailableRooms("");
        try {
            encoder.encode(availableRooms);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfUsersInChatMessageIsEncoded() {
        IServerMessage usersInChat = new MsgUsersInChat(0);
        try {
            encoder.encode(usersInChat);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }

    public void testIfSetGroupIdIsEncoded() {
        IServerMessage setGroupId = new MsgSetGroupId("");
        try {
            encoder.encode(setGroupId);
        } catch (JSONException e) {
            e.printStackTrace();
            fail("JSONException was caught");
        }
    }
}