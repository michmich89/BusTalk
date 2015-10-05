package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgNewUserInChat implements IServerMessage {
    private IUser user;
    private String chatID;

    public MsgNewUserInChat(IUser user, String chatID){
        this.user = user;
        this.chatID = chatID;
    }
}
