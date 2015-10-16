package com.busgen.bustalk.service;

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
public class PlatformDecoder {

    public String decodePlatformObject(JSONObject object){
        String decodedData = new String();
       /* try{

        }catch(JSONException e){
            e.printStackTrace();
        }*/
        return decodedData;
    }
}
