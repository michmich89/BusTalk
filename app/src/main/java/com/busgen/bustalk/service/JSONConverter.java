package com.busgen.bustalk.service;

import android.os.Message;

import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Class used to convert JSON objects from our Server and the Innovation platform to appropriate data types.
 */
public class JSONConverter {

    public IServerMessage decodeServerMessage(JSONObject object){
        IServerMessage message;
        try{
            String type = object.getString("type");
            if(type.equals("chatmessage"))){
                String dateString = object.getString("timestamp");
                Date testDate = new Date();
                //needs to convert timestamp to date?
                message = new MsgChatMessage(object.getString("message"), object.getInt("chatID"),object.getString("nickname"), testDate));
            }else if(type.equals("NewChatRoom")){
                message = new MsgNewChatRoom(object.getInt("chatID"));
            }else if(type.equals("LostChatRoom")){
                message = new MsgLostChatRoom(object.getInt("chatID"));
            }else if(type.equals("NicknameAvailable")){
                message = new MsgNicknameAvailable(object.getBoolean("availability"));
            }else if(type.equals("UserList")){
                //logik för att skapa user meddelanden
            }else if(type.equals("NewUserInChat")){
                IUser = new User()
                message = new MsgNewUserInChat()
                //Samma implementation som ovan utan loop genom lista typ.
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        if(message == null){
            throw new NullPointerException("JSON object could not be converted to message");
        }
        return message;
    }

    public JSONObject encodeServerMessage(IServerMessage message){
        if(message == null){
            throw new NullPointerException("The message was null and could therefore not be converted to a JSON object");
        }
        JSONObject object;
        try{
            if(message instanceof MsgChatMessage){
                chatMessage = (MsgChatMessage) message;
                object.put("type", "chatmessage");
                object.put("chatID", chatMessage.getChatID());
                object.put("nickname", chatMessage.getUser().getNickname());
                object.put("message", chatMessage.getMessage());
                object.put("timestamp", chatMessage.getTimestamp());

            }else if(message instanceof MsgJoinRoom){

            }else if(message instanceof MsgCreateRoom){

            }else if(message instanceof MsgLeaveRoom){

            }else if(message instanceof MsgChooseNickname){

            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public String decodePlatformObject(JSONObject object){
        String decodedData;
        try{

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
