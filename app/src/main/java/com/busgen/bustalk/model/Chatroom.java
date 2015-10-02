package com.busgen.bustalk.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Johan on 2015-10-02.
 */
public class Chatroom implements IChatroom {

    private String chatID;
    private String type;
    private String title;
    private Collection<IUser> users;
    private Collection<IMessage> messages;

    public Chatroom(String chatID, String type, String title){
        this.chatID = chatID;
        this.type = type;
        this.title = title;

        users = new ArrayList<IUser>();
        messages = new ArrayList<IMessage>();

    }

    @Override
    public String getChatID() {
        return chatID;
    }

    @Override
    public void setID(String chatID) {
        this.chatID = chatID;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void addUser(IUser user) {
        users.add(user);
    }

    @Override
    public void removeUser(IUser user) {
        users.remove(user);
    }

    @Override
    public boolean containsUser(IUser user) {
        return users.contains(user);
    }

    @Override
    public Collection<IUser> getUsers() {
        return users;
    }

    @Override
    public Collection<IMessage> getMessages() {
        return messages;
    }

    @Override
    public void terminate() {
        //@TODO
    }
}
