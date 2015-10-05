package com.busgen.bustalk.model;

import java.util.List;

/**
 * Created by Johan on 2015-10-05.
 */
public class Client implements IClient{

    private IUser user;
    private List<IChatroom> chatrooms;
    private Client instance;

    private Client (IUser user){

    }

    public Client (IUser user){
        if (this.instance.equals(null)){
            return new 
        }
    }

    @Override
    public IUser getUser() {
        return null;
    }

    @Override
    public IChatroom[] getChatrooms() {
        return chatrooms;
    }

    @Override
    public List<IServerMessage> recieveMessages() {
        return null;
    }

    @Override
    public void sendMessage(IServerMessage serverMessage) {

    }
}
