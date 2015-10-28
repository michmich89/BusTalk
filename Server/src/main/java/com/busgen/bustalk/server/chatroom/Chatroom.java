package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.user.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all important data related to a chat room.
 *
 * Created by Kristoffer on 2015-09-29.
 */
public class Chatroom implements IChatroom {

    //TODO: We dont have any way of making a private chatroom

    private String chatroomTitle;
    private final int idNbr;
    private final List<IUser> chatroomUsers;

    public Chatroom(int idNbr, String chatroomTitle){
        this.chatroomTitle = chatroomTitle;
        this.idNbr = idNbr;
        chatroomUsers = new ArrayList<IUser>();
    }

    public void subscribeToRoom(IUser user){
        chatroomUsers.add(user);

    }

    public void unsubscribeToRoom(IUser user){
        chatroomUsers.remove(user);
    }

    public int getIdNbr(){
        return this.idNbr;
    }

    public String getTitle () {
        return this.chatroomTitle;
    }

    public List<IUser> getChatroomUsers(){
        return new ArrayList<IUser>(chatroomUsers);
    }

    public boolean isUserInRoom(IUser user){
        return chatroomUsers.contains(user);
    }
}
