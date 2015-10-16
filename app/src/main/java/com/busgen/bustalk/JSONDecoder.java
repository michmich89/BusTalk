package com.busgen.bustalk;

import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChat;
import com.busgen.bustalk.model.User;
import com.busgen.bustalk.utils.MessageTypes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

/**
 * Created by nalex on 16/10/2015.
 */
public class JSONDecoder implements Decoder.Text<IServerMessage> {

    JSONObject jsonObject;
    IServerMessage serverMessage;

    @Override
    public IServerMessage decode(String s) throws DecodeException {
        return serverMessage;
    }

    @Override
    public boolean willDecode(String s) {
        try{
            jsonObject = new JSONObject(s);
            IServerMessage serverMessage = null;
            int type = jsonObject.getInt("type");
            if(type == MessageTypes.CHAT_MESSAGE_NOTIFICATION)){

                Date testDate = new Date();
                //todo needs to convert timestamp to date och ska hur ska jag ta reda på iSME här?
                serverMessage = new MsgChatMessage(true, jsonObject.getString("message"),  jsonObject.getString("time"), jsonObject.getString("sender"), jsonObject.getInt("chatId"));
            }else if(type == MessageTypes.ROOM_CREATED_NOTIFICATION){
                serverMessage = new MsgNewChatRoom(jsonObject.getInt("chatId"));
            }else if(type == MessageTypes.ROOM_DELETED_NOTIFICATION){
                serverMessage = new MsgLostChatRoom(jsonObject.getInt("chatId"));
            } else if(type == MessageTypes.NAME_AND_INTEREST_SET){
                serverMessage = new MsgNicknameAvailable(jsonObject.getBoolean("succeeded"));
            }else if(type == MessageTypes.LIST_OF_USERS_IN_CHAT_NOTIFICATION){
                //todo logik för att skapa user meddelanden, behöver se JSON objektet för att implementera
                JSONArray array = jsonObject.getJSONArray("users");
                MsgUsersInChat usersInChat = new MsgUsersInChat(jsonObject.getInt("chatId"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject userObject = array.getJSONObject(i);
                    IUser user = new User(userObject.getString("sender"), userObject.getString("interests"));
                    usersInChat.addUserToList(user);
                }
            }else if(type == MessageTypes.NEW_USER_IN_CHAT_NOTIFICATION) {
                IUser user = new User(jsonObject.getString("sender"), jsonObject.getString("interests"));
                serverMessage = new MsgNewUserInChat(user, jsonObject.getInt("chatId"));
            }else if(type == MessageTypes.USER_LEFT_ROOM_NOTIFICATION){
                //todo behövs meddelande för detta.
            }else if(type == MessageTypes.LIST_OF_CHATROOMS_NOTIFICATION){
                //todo fixa flera room created?
                JSONArray array = jsonObject.getJSONArray("users");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject roomObject = array.getJSONObject(i);

                }
            }

            //Kanske onödig
            if(serverMessage == null){
                throw new NullPointerException("JSON object could not be converted to message");
            }
            return true;

        }catch(JSONException e){
            return false;
        }
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
