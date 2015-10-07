package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.Collection;

/**
 * Interface representing a a Client connected to the Chat server.
 */
public interface IClient {

    IUser getUser();
    String getNickname();
    String getInterests();
    Collection<IChatroom> getChatrooms();

    void setUser(IUser user);
    void setNickname(String nickname);
    void setInterests(String interests);

    Collection<IServerMessage> recieveMessages();
    void sendMessage(IServerMessage serverMessage);

    void joinRoom (IChatroom chatroom);
    void leaveRoom (IChatroom chatroom);
}
