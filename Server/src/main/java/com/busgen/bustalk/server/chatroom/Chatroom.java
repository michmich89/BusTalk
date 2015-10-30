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

    public Chatroom(int idNbr, String chatroomTitle){
        this.chatroomTitle = chatroomTitle;
        this.idNbr = idNbr;
    }

    public int getIdNbr(){
        return this.idNbr;
    }

    public String getTitle () {
        return this.chatroomTitle;
    }
}
