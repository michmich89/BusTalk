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
            if(type.equals("chatmessage")){
                String dateString = object.getString("timestamp");
                Date testDate = new Date();
                //todo needs to convert timestamp to date?
                message = new MsgChatMessage(object.getString("message"), object.getInt("chatID"),object.getString("nickname"), testDate));
            }else if(type.equals("NewChatRoom")){
                message = new MsgNewChatRoom(object.getInt("chatID"));
            }else if(type.equals("LostChatRoom")){
                message = new MsgLostChatRoom(object.getInt("chatID"));
            }else if(type.equals("NicknameAvailable")){
                message = new MsgNicknameAvailable(object.getBoolean("availability"));
            }else if(type.equals("UserList")){
                //todo logik för att skapa user meddelanden, behöver se JSON objektet för att implementera
            }else if(type.equals("NewUserInChat")){
                IUser user = new User(object.getString("nickname"), object.getString("interests"));
                message = new MsgNewUserInChat(user, object.getInt("chatID"));
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        /*
        Kanske onödig
        if(message == null){
            throw new NullPointerException("JSON object could not be converted to message");
        }*/
        return message;
    }

    public JSONObject encodeServerMessage(IServerMessage message){
        if(message == null){
            throw new NullPointerException("The message was null and could therefore not be converted to a JSON object");
        }
        JSONObject object = new JSONObject();
        try{
            if(message instanceof MsgChatMessage){
                MsgChatMessage chatMessage = (MsgChatMessage) message;
                object.put("type", "chatmessage");
                object.put("chatID", chatMessage.chatID());
                object.put("nickname", chatMessage.getNickname());
                object.put("message", chatMessage.getMessage());
                object.put("timestamp", chatMessage.getTimestamp());
                //// TODO: 05/10/2015 Implementera metoder(if-satser nedanför

            }else if(message instanceof MsgJoinRoom){

            }else if(message instanceof MsgCreateRoom){

            }else if(message instanceof MsgLeaveRoom){

            }else if(message instanceof MsgChooseNickname){

            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return object;
    }

    public String decodePlatformObject(JSONObject object){
        String decodedData;
        try{

        }catch(JSONException e){
            e.printStackTrace();
        }
    }
}
