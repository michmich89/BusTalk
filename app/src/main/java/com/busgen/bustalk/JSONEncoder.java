package com.busgen.bustalk;

import android.util.Log;

import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRoomsRequest;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgSetGroupId;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChatRequest;
import com.busgen.bustalk.utils.MessageTypes;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nalex on 16/10/2015.
 */
public class JSONEncoder {

    public String encode(IServerMessage clientMessage) {
        //Maybe should have "return clientMessage.toString();" instead
        if(clientMessage == null){
            throw new NullPointerException("The message was null and could therefore not be converted to a JSON object");
        }
        JSONObject object = new JSONObject();
        try{
            if(clientMessage instanceof MsgChatMessage){
                //todo chatmeddelande för skicka ett för hämta
                MsgChatMessage chatMessage = (MsgChatMessage) clientMessage;
                object.put("type", MessageTypes.CHAT_MESSAGE);
                object.put("chatId", chatMessage.getChatId());
                object.put("message", chatMessage.getMessage());
                /*
                object.put("sender", chatMessage.getNickname());
                object.put("time", chatMessage.getDate().toString());
                */
            }else if(clientMessage instanceof MsgJoinRoom){
                MsgJoinRoom joinMessage = (MsgJoinRoom)clientMessage;
                object.put("type", MessageTypes.JOIN_ROOM_REQUEST);
                object.put("chatId",joinMessage.getChatID());

            }else if(clientMessage instanceof MsgCreateRoom){
                MsgCreateRoom createMessage = (MsgCreateRoom)clientMessage;
                object.put("type",MessageTypes.CREATE_ROOM_REQUEST);
                object.put("chatId", createMessage.getChatID());
                object.put("chatName", createMessage.getChatName());

            }else if(clientMessage instanceof MsgLeaveRoom){
                MsgLeaveRoom leaveMessage = (MsgLeaveRoom)clientMessage;
                object.put("type", MessageTypes.LEAVE_ROOM_REQUEST);
                object.put("chatId", leaveMessage.getChatID());

            }else if(clientMessage instanceof MsgChooseNickname){
                MsgChooseNickname nickMessage = (MsgChooseNickname)clientMessage;
                object.put("type", MessageTypes.CHOOSE_NICKNAME_REQUEST);
                object.put("name", nickMessage.getNickname());
                object.put("interests", nickMessage.getInterests());
            }else if(clientMessage instanceof MsgAvailableRoomsRequest){
                object.put("type", MessageTypes.LIST_OF_ALL_CHATROOMS_REQUEST);
                Log.d("MyTag", "" + "encoder LIST_OF_ALL_CHATROOMS_REQUEST");
            }else if(clientMessage instanceof MsgUsersInChatRequest){
                MsgUsersInChatRequest usersInChat = (MsgUsersInChatRequest)clientMessage;
                object.put("type", MessageTypes.LIST_OF_USERS_IN_ROOM_REQUEST);
                object.put("chatId", usersInChat.getChatID());
            }else if(clientMessage instanceof MsgSetGroupId){
                MsgSetGroupId setGroupId = (MsgSetGroupId)clientMessage;
                object.put("type", MessageTypes.CHANGE_GROUP_ID);
                object.put("groupId", setGroupId.getGroupId());
            }
        }catch(JSONException e){
            e.printStackTrace();
        }
        return object.toString();
    }
}
