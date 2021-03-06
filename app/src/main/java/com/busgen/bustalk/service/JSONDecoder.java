package com.busgen.bustalk.service;

import android.util.Log;

import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRooms;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChat;
import com.busgen.bustalk.model.User;
import com.busgen.bustalk.utils.MessageTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to decode JSON string to ServerMessages.
 */
public class JSONDecoder {

    private JSONObject jsonObject;
    private IServerMessage serverMessage;

    public IServerMessage decode(String s) {
        return serverMessage;
    }

    public boolean willDecode(String s) {
        try{
            jsonObject = new JSONObject(s);
            serverMessage = null;
            int type = jsonObject.getInt("type");

            if(type == MessageTypes.CHAT_MESSAGE_NOTIFICATION){
                serverMessage = new MsgChatMessage(jsonObject.getBoolean("isMe"), jsonObject.getString("message"),  jsonObject.getString("time"), jsonObject.getString("sender"), jsonObject.getInt("chatId"));
            }else if(type == MessageTypes.ROOM_CREATED_NOTIFICATION){
                serverMessage = new MsgNewChatRoom(jsonObject.getInt("chatId"), jsonObject.getString("title"), jsonObject.getBoolean("isYours"));
            }else if(type == MessageTypes.ROOM_DELETED_NOTIFICATION){
                serverMessage = new MsgLostChatRoom(jsonObject.getInt("chatId"));
            } else if(type == MessageTypes.NAME_AND_INTEREST_SET){
                serverMessage = new MsgNicknameAvailable(jsonObject.getBoolean("succeeded"));
            }else if(type == MessageTypes.LIST_OF_USERS_IN_CHAT_NOTIFICATION){
                JSONArray array = jsonObject.getJSONArray("users");
                MsgUsersInChat usersInChat = new MsgUsersInChat(jsonObject.getInt("chatId"));
                usersInChat.addUsersToList(getUsersFromJson(array));
                serverMessage = usersInChat;
            }else if(type == MessageTypes.NEW_USER_IN_CHAT_NOTIFICATION) {
                IUser user = new User(jsonObject.getString("name"), jsonObject.getString("interests"));
                serverMessage = new MsgNewUserInChat(user, jsonObject.getInt("chatId"));
            }else if(type == MessageTypes.USER_LEFT_ROOM_NOTIFICATION){
                IUser user = new User(jsonObject.getString("name"), "");
                serverMessage = new MsgLostUserInChat(jsonObject.getInt("chatId"), user);
            }else if(type == MessageTypes.LIST_OF_CHATROOMS_NOTIFICATION){
                JSONArray array = jsonObject.getJSONArray("chatrooms");
                MsgAvailableRooms rooms = new MsgAvailableRooms(jsonObject.getString("groupId"));
                rooms.addRoomsToList(getRoomsFromJson(array));
                serverMessage = rooms;
            }

            if(serverMessage == null){
                throw new NullPointerException("JSON object could not be converted to message");
            }
            return true;

        }catch(JSONException e){
            return false;
        }
    }

    private List<IChatroom> getRoomsFromJson(JSONArray array) throws JSONException {
        List<IChatroom> chatrooms = new ArrayList<IChatroom>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject roomObject = array.getJSONObject(i);
            Chatroom chatroom = new Chatroom(roomObject.getInt("chatId"), roomObject.getString("name"));
            chatrooms.add(chatroom);
        }
        return chatrooms;
    }

    private List<IUser> getUsersFromJson(JSONArray array) throws JSONException {
        List<IUser> users = new ArrayList<IUser>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject userObject = array.getJSONObject(i);
            IUser user = new User(userObject.getString("name"), userObject.getString("interests"));
            users.add(user);
        }
        return users;
    }
}
