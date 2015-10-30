package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgCreateRoom implements IServerMessage {
    private int chatID;
    private String chatName;

    public MsgCreateRoom(int chatID, String chatName){
        this.chatID = chatID;
        this.chatName = chatName;
    }

    public int getChatID(){ return chatID;}
    public String getChatName(){
        return chatName;
    }
}
