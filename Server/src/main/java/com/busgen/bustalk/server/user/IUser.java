package com.busgen.bustalk.server.user;

import com.busgen.bustalk.server.chatroom.IChatroom;

import java.util.List;

/**
 * Interface for a user.
 *
 * Created by Kristoffer on 2015-10-12.
 */
public interface IUser {

    public String getName();
    public void setName(String newName);
    public String getInterests();
    public void setInterests(String newInterests);
}
