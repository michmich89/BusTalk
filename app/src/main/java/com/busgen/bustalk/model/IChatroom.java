package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.Collection;
import java.util.List;

/**
 * Interface representing a Chatroom.
 */
public interface IChatroom {

    int getChatID();

    void setID(int chatID);

    void setTitle(String title);

    String getTitle();

    void addUser(IUser user);

    void removeUser(IUser user);

    boolean containsUser(IUser user);

    int getNbrOfUsers();

    boolean isEmpty();

    Collection<IUser> getUsers();

    List<MsgChatMessage> getMessages();

    void terminate();

    void addMessage(MsgChatMessage message);

}
