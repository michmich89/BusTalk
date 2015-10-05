package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.Collection;

/**
 * Interface representing a a Client connected to the Chat server.
 */
public interface IClient {

    IUser getUser();
    Collection<IChatroom> getChatrooms();

    Collection<IServerMessage> recieveMessages();
    void sendMessage(IServerMessage serverMessage);

    void joinRoom (IChatroom chatroom);
    void leaveRoom (IChatroom chatroom);
}
