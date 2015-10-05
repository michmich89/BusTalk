package com.busgen.bustalk.service;

import com.busgen.bustalk.model.IMessage;
import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.Message;
import com.busgen.bustalk.model.User;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to convert JSON objects from our Server and the Innovation platform to appropriate data types.
 */
public class JSONConverter {

    public IMessage decodeServerMessage(JSONObject object){
        Message message;
        try{
            String type = object.getString("type");
            if(type.equals("chatmessage"))){
                //IUser user = new User(object.getString("nick"), object.getString("interest"));
                message = new Message(type, object.getString("nick") object.getInt("chatID"), object.getString("message"), object.getString("timestamp")));
            }else if(type.equals("NewChatRoom")||type.equals("LostChatRoom")){
                message = new Message(type, object.getInt("chatID"));
            }else if(type.equals("NickAvailable")){
                message = new Message(type, object.getBoolean("availability"));
            }else if(type.equals("UserList")){
                //logik för att skapa user meddelanden
            }else if(type.equals("NewUserInChat")){
                //Samma implementation som ovan utan loop genom lista typ.
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
    }

    public JSONObject encodeServerMessage(IMessage message){
        if(message == null){
            throw new NullPointerException("The message was null and could therefore not be converted to a JSON object");
        }
        JSONObject object;
        try{
            if(message instanceof MsgChatMessage){
                chatMessage = (MsgChatMessage) message;
                object.put("type", "chatmessage");
                object.put("", message.)

            }else if(message instanceof MsgJoinRoom){

            }else if(message instanceof MsgCreateRoom){

            }else if(message instanceof MsgLeaveRoom){

            }else if(message instanceof MsgChooseNick){

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
