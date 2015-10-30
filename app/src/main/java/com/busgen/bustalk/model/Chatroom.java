package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johan on 2015-10-02.
 */
public class Chatroom implements IChatroom, Serializable {

    private int chatID;
    private String title;
    private List<IUser> users;
    private List<MsgChatMessage> messages;

    public Chatroom(int chatID, String title){
        this.chatID = chatID;
        this.title = title;
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
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void addUser(IUser user) {
        if (user != null && !users.contains(user)){
            users.add(user);
        }
    }

    @Override
    public void removeUser(IUser user) {
        if (user != null && users.contains(user)){

            users.remove(user);
        }
    }

    @Override
    public void setUsers(List<IUser> users){
        this.users = users;
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
    public List<IUser> getUsers() {
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
    public boolean equals(Object object){

        //Borde den kolla något annat?
        if ((object instanceof IChatroom) && this.getChatID() == ((IChatroom)object).getChatID()){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = chatID * 11;
        return hashCode;
    }
}
