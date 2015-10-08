package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Johan on 2015-10-02.
 */
public class Chatroom implements IChatroom {

    private int chatID;
    private String type;
    private String title;
    private List<IUser> users;
    private List<MsgChatMessage> messages;
    private int maxUsers;

    public Chatroom(int chatID, String type, String title, int maxUsers){
        this.chatID = chatID;
        this.type = type;
        this.title = title;
        this.maxUsers = maxUsers;
        users = new ArrayList<IUser>();
        messages = new ArrayList<MsgChatMessage>();

    }

    @Override
    public int getChatID() {
        return chatID;
    }

    @Override
    public void setID(int chatID) {
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
    public int getNbrOfUsers() {
        return users.size();
    }

    @Override
    public boolean isEmpty() {
        return (users.size() == 0);
    }

    @Override
    public boolean isFull() {
        return (users.size() >= maxUsers);
    }

    @Override
    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    @Override
    public int getMaxUsers() {
        return maxUsers;
    }

    @Override
    public Collection<IUser> getUsers() {
        return users;
    }

    @Override
    public List<MsgChatMessage> getMessages() {
        return messages;
    }

    @Override
    public void terminate() {
    }

    @Override
    public void addMessage(MsgChatMessage message) {
        messages.add(message);
    }
}
