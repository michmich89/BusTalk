package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgNewChatRoom implements IServerMessage {

    private int chatID;
    private String title;
    private boolean isYours;

    public MsgNewChatRoom(int chatID, String title, boolean isYours){
        this.chatID = chatID;
        this.title = title;
        this.isYours = isYours;
    }

    public int getChatID(){
        return chatID;
    }
    public String getTitle(){
        return title;
    }
    /*
    Indicates whether the client created the chatroom or not.
     */
    public boolean isClientCreator(){
        return isYours;
    }
}
