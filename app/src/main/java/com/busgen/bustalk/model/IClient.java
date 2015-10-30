package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.service.EventBus;

import java.util.Collection;

/**
 * Interface representing a a Client connected to the Chat server.
 */
public interface IClient {

    IUser getUser();
    String getUserName();
    String getInterest();
    Collection<IChatroom> getChatrooms();
    EventBus getEventBus();

    void setUser(IUser user);
    void setUserName(String nickname);
    void setInterest(String interests);
    void setEventBus(EventBus eventBus);
    Collection<IServerMessage> recieveMessages();
    void sendMessage(IServerMessage serverMessage);


    void joinRoom (IChatroom chatroom);
    void leaveRoom (IChatroom chatroom);
}
