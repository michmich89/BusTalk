package com.busgen.bustalk.model;

import java.util.Collection;

/**
 * Interface representing a Chatroom.
 */
public interface IChatroom {

    public String getChatID();

    public void setID(String chatID);

    public String getType();

    public void setType(String type);

    public void setTitle(String title);

    public String getTitle();

    public void addUser(IUser user);

    public void removeUser(IUser user);

    public boolean containsUser(IUser user);

    public Collection<IUser> getUsers();

    public Collection<IMessage> getMessages();

    public void terminate();

}
