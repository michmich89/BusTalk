package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgLostChatRoom implements IServerMessage {

    private int chatID;

    public MsgLostChatRoom(int chatID){
        this.chatID = chatID;
    }

    public int getChatID(){
        return chatID;
    }
}
