package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.service.EventBus;

import java.util.Collection;
import java.util.List;

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

    void setUsers(int chatId, List<IUser> userList);
    void addUser(int chatId, IUser user);
    void removeUser(int chatId, IUser user);
    void requestUsersFromServer(int chatId);
    void setGroupId(String groupId);
    String getGroupId();

    void joinRoom (IChatroom chatroom);
    void leaveRoom (IChatroom chatroom);
    void leaveRoom(int chatId);
}
