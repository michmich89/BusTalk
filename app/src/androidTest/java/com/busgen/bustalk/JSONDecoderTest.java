package com.busgen.bustalk;

import com.busgen.bustalk.utils.MessageTypes;

import junit.framework.TestCase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONDecoderTest extends TestCase{
    private JSONDecoder decoder;
    private JSONObject jsonObject;

    @Override
    public void setUp() {
        decoder = new JSONDecoder();
    }

    public void testIfRoomCreatedMessageIsDecoded() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.ROOM_CREATED_NOTIFICATION);
        jsonObject.put("title", "test title");
        jsonObject.put("chatId", 0);
        jsonObject.put("isYours", false);

        assertTrue(willDecode(jsonObject));
    }

    public void testIfUserJoinedMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.NEW_USER_IN_CHAT_NOTIFICATION);
        jsonObject.put("chatId", 0);
        jsonObject.put("name", "username");
        jsonObject.put("interests", "interests");

        assertTrue(willDecode(jsonObject));
    }

    public void testIfUserLeftMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.USER_LEFT_ROOM_NOTIFICATION);
        jsonObject.put("chatId", 0);
        jsonObject.put("name", "username");

        assertTrue(willDecode(jsonObject));
    }

    public void testIfRoomDeletedMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.ROOM_DELETED_NOTIFICATION);
        jsonObject.put("chatId", 0);

        assertTrue(willDecode(jsonObject));
    }

    public void testIfChatroomListMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.LIST_OF_CHATROOMS_NOTIFICATION);
        jsonObject.put("groupId", "group");
        JSONObject chatroom = new JSONObject();
        chatroom.put("chatId", 0);
        chatroom.put("name", "123");
        jsonObject.put("chatrooms", new JSONArray().put(chatroom));

        assertTrue(willDecode(jsonObject));
    }

    public void testIfUserListMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.LIST_OF_USERS_IN_CHAT_NOTIFICATION);
        jsonObject.put("chatId", 0);
        JSONObject user = new JSONObject();
        user.put("name", "name");
        user.put("interests", "interests");
        jsonObject.put("users", new JSONArray().put(user));

        assertTrue(willDecode(jsonObject));
    }

    public void testIfChatMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.CHAT_MESSAGE_NOTIFICATION);
        jsonObject.put("chatId", 0);
        jsonObject.put("sender", "sender");
        jsonObject.put("message", "hej");
        jsonObject.put("time", "random date");
        jsonObject.put("isMe", false);

        assertTrue(willDecode(jsonObject));
    }

    public void testIfNameAndInterestMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.NAME_AND_INTEREST_SET);
        jsonObject.put("succeeded", true);

        assertTrue(willDecode(jsonObject));
    }

    public void testIfInvalidMessageIsDecoded() throws JSONException {
        jsonObject = new JSONObject();
        jsonObject.put("type", MessageTypes.NAME_AND_INTEREST_SET);
        jsonObject.put("invalid", true);

        assertFalse(willDecode(jsonObject));
    }

    private boolean willDecode(JSONObject jsonObject) {
        return decoder.willDecode(jsonObject.toString());
    }
}