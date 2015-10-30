package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgUsersInChatRequest implements IServerMessage{
    private int chatID;

    public MsgUsersInChatRequest(int chatID){
        this.chatID = chatID;
    }

    public int getChatID(){
        return chatID;
    }
}
