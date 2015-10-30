package com.busgen.bustalk.model.ServerMessages;

import android.util.Log;

import com.busgen.bustalk.model.IServerMessage;

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

    @Override
    public boolean equals(Object object){
    MsgChatMessage chatMessage;
        Log.d("MyTag", "equalling chatmessage");
        if(object != null && object instanceof MsgChatMessage){
            chatMessage = (MsgChatMessage) object;
            Log.d("MyTag", "chatMessage not null");
            if ((this.isMe == chatMessage.getIsMe() && this.getMessage().equals(chatMessage.getMessage())
                    && this.date.equals(chatMessage.getDate()) && this.userName.equals(chatMessage.getUserName())
                    && this.chatId == chatMessage.getChatId())){
                Log.d("MyTag", "equals is true");
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        int isMe;
        int message;
        int date;
        int userName;
        int chatId;

        if (this.isMe = true){
            isMe = 1;
        } else {
            isMe = 0;
        }

        message = this.message.hashCode();
        date = this.date.hashCode();
        userName = this.userName.hashCode();
        chatId = this.userName.hashCode();

        int hashCode = (isMe + message + date + userName + chatId);
        return hashCode;
    }
}
