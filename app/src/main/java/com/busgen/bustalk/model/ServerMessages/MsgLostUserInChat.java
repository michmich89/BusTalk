package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgLostUserInChat implements IServerMessage {

    private String chatID;
    private IUser user;

    public MsgLostUserInChat(String chatID, IUser user){
        this.chatID = chatID;
        this.user = user;
    }
}
