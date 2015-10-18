package com.busgen.bustalk.server.user;

import com.busgen.bustalk.server.chatroom.Chatroom;

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

    /**
     * Called upon when a user joins a chatroom, and saves said chatroom to a list obtainable through
     * <b>getCurrentChatrooms()</b>
     *
     * @param chatroom The chatroom the user joined
     */
    public void onJoinChatroom(Chatroom chatroom);

    /**
     * Called upon when a user leaves a chatroom
     *
     * @param chatroom The chatroom the user leaves
     */
    public void onLeaveChatroom(Chatroom chatroom);

    public List<Chatroom> getCurrentChatrooms();


}
