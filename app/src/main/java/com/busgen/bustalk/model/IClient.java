package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.Collection;

/**
 * Interface representing a a Client connected to the Chat server.
 */
public interface IClient {

    IUser getUser();
    String getUserName();
    String getInterest();
    Collection<IChatroom> getChatrooms();

    void setUser(IUser user);
    void setUserName(String nickname);
    void setInterest(String interests);

    Collection<IServerMessage> recieveMessages();
    void sendMessage(IServerMessage serverMessage);

    void joinRoom (IChatroom chatroom);
    void leaveRoom (IChatroom chatroom);

    void addMessageToChatroom(MsgChatMessage message);
}
