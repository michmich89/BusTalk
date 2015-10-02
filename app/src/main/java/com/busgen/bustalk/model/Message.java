package com.busgen.bustalk.model;

/**
 * Created by Johan on 2015-10-01.
 */
public class Message implements IMessage {

    private String type;
    private String message;
    private int chatID;
    private IUser user;

    /*
    creates a chat message
     */
    public Message(String type, String message, int chatID, IUser user){
        this.type = type;
        this.message = message;
        this.chatID = chatID;
        this.user = user;
    }

    /*
    creates a command message
     */
    public Message(String type, int chatID, IUser user){
        this.type = type;
        this.chatID = chatID;
        this.user = user;
    }

    /*
    creates a command message
     */
    public Message(String type, IUser user) {
        this.type = type;
        this.user = user;
    }

    @Override
    public String getType(){
        return this.type;
    }

    @Override
    public String getMessage(){
        return this.message;
    }

    @Override
    public int getChatID(){
        return this.chatID;
    }

    @Override
    public IUser getUser(){
        return this.user;
    }

    @Override
    public String toString(){
        return ("User: " + user + " Type: " + type + " Message: " + message + " ID:" +  Integer.toString(chatID));
    } //Skriver den ut null om någon variabel är null?

}
