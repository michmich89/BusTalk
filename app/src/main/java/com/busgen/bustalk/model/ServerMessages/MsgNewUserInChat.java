package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgNewUserInChat implements IServerMessage {
    private String message;
    private String chatID;
    private String nickname;
    private Date timestamp;

    public MsgChatMessage(String message, String chatID, String nickname, Date timestamp){
        this.message = message;
        this.chatID = chatID;
        this.nickname = nickname;
        this.timestamp = timestamp;
    }
}
