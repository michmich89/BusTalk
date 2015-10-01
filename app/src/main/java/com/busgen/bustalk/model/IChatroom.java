package com.busgen.bustalk.model;

import java.util.List;

/**
 * Interface representing a Chatroom.
 */
public interface IChatroom {
    public List<IUser> getUser();

    public List<IMessage> getMessages();

}
