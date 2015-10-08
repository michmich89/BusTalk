package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

import java.util.Date;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgChatMessage implements IServerMessage {

    private String message;
    private int chatId;
    private String userName;
    private String date;
    private boolean isMe;

    public MsgChatMessage(boolean isMe, String message, String date, String userName, int chatId) {
        this.isMe = isMe;
        this.message = message;
        this.date = date;
        this.userName = userName;
        this.chatId = chatId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getChatId() {
        return chatId;
    }

    public void setChatId(int chatId) {
        this.chatId = chatId;
    }

    public boolean getIsMe() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
