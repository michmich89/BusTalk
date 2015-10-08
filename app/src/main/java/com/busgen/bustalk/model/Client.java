package com.busgen.bustalk.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Johan on 2015-10-05.
 */
public class Client implements IClient{

    private IUser user;
    private Collection<IChatroom> chatrooms;

    public Client (IUser user){
        this.user = user;
        chatrooms = new ArrayList<IChatroom>();
    }

    @Override
    public String getNickname() {
        return user.getNickname();
    }

    @Override
    public String getInterests() {
        return user.getInterests();
    }

    @Override
    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public void setNickname(String nickname) {
        user.setNickname(nickname);
    }

    @Override
    public void setInterests(String interests) {
        user.setInterests(interests);
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Collection<IChatroom> getChatrooms() {
        return chatrooms;
    }

    @Override
    public Collection<IServerMessage> recieveMessages() {
        return null;
    }

    @Override
    public void sendMessage(IServerMessage serverMessage) {

    }

    @Override
    public void joinRoom(IChatroom chatroom) {
        chatrooms.add(chatroom);
    }

    @Override
    public void leaveRoom(IChatroom chatroom) {
        if (chatrooms.contains(chatroom)){
            chatrooms.remove(chatroom);
        }
    }
}
