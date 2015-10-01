package com.busgen.bustalk.model;

/**
 * Message sent by a user in a chatroom.
 */
public interface IMessage {

    String getType();
    String getMessage();
    int getChatID();
    IUser getUser();
    String toString();
    void parse();

}
