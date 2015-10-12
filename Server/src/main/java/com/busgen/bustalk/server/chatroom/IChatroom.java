package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.user.User;

import java.util.List;

/**
 * Created by Kristoffer on 2015-10-12.
 */
public interface IChatroom {

    /**
     * Method will add user to a chatroom, if user is not already subscribed to room
     *
     * @param user - the user you want to add
     * @return true if user was just added - false if user was already subscribed to room
     */
    public boolean subscribeToRoom(User user);

    /**
     * Method will remove a user from a chatroom if the user is subscribed to that room
     *
     * @param user - the user who wants to leave a room
     * @return true if user was able to be removed - false if the user wasn't subscribed to the room
     */
    public boolean unsubscribeToRoom(User user);

    public int getIdNbr();
    public String getTitle();
    public List<User> getChatroomUsers();
}
