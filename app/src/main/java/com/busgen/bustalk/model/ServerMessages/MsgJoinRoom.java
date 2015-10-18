package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgJoinRoom implements IServerMessage {

    private int chatID;
    private IChatroom chatroom;

    public MsgJoinRoom(IChatroom chatroom){
     //   this.chatID = chatID;
        this.chatroom = chatroom;
    }

    public int getChatID(){
        return chatID;
    }

    public IChatroom getChatroom(){
        return chatroom;
    }
}
