package com.busgen.bustalk.model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Johan on 2015-10-05.
 */
public class Client implements IClient{

    private IUser user;
    private Collection<IChatroom> chatrooms;
    private volatile static Client instance;

    private Client (IUser user){
        this.user = user;
        chatrooms = new ArrayList<IChatroom>();
    }

    public static Client getInstance(IUser user){

        if (instance == null){
            synchronized (Client.class) {
                if (instance == null) {
                    instance = new Client(user);
                }
            }
        }
        return instance;
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
