package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

import java.util.Date;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgChatMessage implements IServerMessage {

    private String message;
    private int chatID;
    private String nickname;
    private Date timestamp;

    public MsgChatMessage(String message, int chatID, String nickname, Date timestamp){
        this.message = message;
        this.chatID = chatID;
        this.nickname = nickname;
        this.timestamp = timestamp;
    }

    public String getMessage(){
        return message;
    }

    public int getChatID(){
        return chatID;
    }

    public String getNickname(){
        return nickname;
    }

    public Date getTimestamp(){
        return timestamp;
    }
}
