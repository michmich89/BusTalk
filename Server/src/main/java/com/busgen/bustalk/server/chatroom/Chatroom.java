package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.user.User;

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
    private final List<User> chatroomUsers;

    public Chatroom(int idNbr, String chatroomTitle){
        this.chatroomTitle = chatroomTitle;
        this.idNbr = idNbr;
        chatroomUsers = new ArrayList<User>();
    }

    public void subscribeToRoom(User user){
        chatroomUsers.add(user);

    }

    public void unsubscribeToRoom(User user){
        chatroomUsers.remove(user);
    }

    public int getIdNbr(){
        return this.idNbr;
    }

    public String getTitle () {
        return this.chatroomTitle;
    }

    public List<User> getChatroomUsers(){
        return new ArrayList<User>(chatroomUsers);
    }

    public boolean isUserInRoom(User user){
        return chatroomUsers.contains(user);
    }
}
