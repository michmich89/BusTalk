package com.busgen.bustalk.model;

import java.util.Date;

/**
 * Created by Johan on 2015-10-01.
 */
public class Message implements IServerMessage {

    private String type;
    private String message;
    private int chatID;
    private IUser user;
    private Date timestamp;

    /*
    creates a chat message
     */
    public Message(String type, String message, int chatID, IUser user, Date timestamp){
        this.type = type;
        this.message = message;
        this.chatID = chatID;
        this.user = user;
    }

    /*
    creates a newUserInChat/lostUserInChat message
     */
    public Message(String type, int chatID, IUser user){
        this.type = type;
        this.chatID = chatID;
        this.user = user;
    }

    /*
    creates a joinRoom/leaveRoom message
     */
    public Message(String type, int chatID) {
        this.type = type;
        this.chatID = chatID;
    }

    @Override
    public String getType(){
        return this.type;
    }


    @Override
    public String toString(){
        return ("User: " + user + " Type: " + type + " Message: " + message + " ID:" +  Integer.toString(chatID));
    } //Skriver den ut null om någon variabel är null?

}
