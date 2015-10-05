package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgNewChatRoom implements IServerMessage {

    private String chatID;

    public MsgNewChatRoom(String chatID){
        this.chatID = chatID;
    }
}
