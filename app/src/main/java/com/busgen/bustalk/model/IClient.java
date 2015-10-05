package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.List;

/**
 * Interface representing a a Client connected to the Chat server.
 */
public interface IClient {

    IUser getUser();
    IChatroom[] getChatrooms();

    List<IServerMessage> recieveMessages();
    void sendMessage(IServerMessage serverMessage);
}
