package com.busgen.bustalk.server.chatroom;

/**
 * Data model of a chatroom
 */
public class Chatroom implements IChatroom {

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
