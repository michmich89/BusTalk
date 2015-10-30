package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.user.IUser;

import java.util.List;

public interface IChatGroup {
    public void joinGroup(IUser user);
    public void leaveGroup(IUser user);
    public boolean isEmpty();
    public boolean userIsInChatroom(IUser user, int chatId);
    public void createRoom(IUser user, String title);
    public void joinRoom(IUser user, int chatId);
    public void leaveRoom(IUser user, int chatId);
    public List<IChatroom> getChatrooms();
    public List<IUser> getUsersInRoom(int chatId);
    public boolean isUserInGroup(IUser user);
    public String getGroupId();
    public List<IUser> getUsers();
    public List<IChatroom> getRoomsForUser(IUser user);
}
