package com.busgen.bustalk.server.user;

import com.busgen.bustalk.server.chatroom.IChatroom;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds all important data related to a user.
 *
 * Created by Kristoffer on 2015-09-29.
 */
public class User implements IUser {

    private String name;
    private String interests;
    private final List<IChatroom> inChatrooms;
    private String groupId;

    public User(String name, String interests){
        this.name = name;
        this.interests = interests;
        inChatrooms = new ArrayList<IChatroom>();
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof User)) return false;

        User user = (User)o;

        return (user.name.equalsIgnoreCase(this.name));
    }


    @Override
    public int hashCode(){
        return name.toLowerCase().hashCode()*17*5*23;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public String getInterests(){
        return this.interests;
    }

    public void setInterests(String newInterests){
        this.interests = newInterests;
    }

    public void onJoinChatroom(IChatroom chatroom){
        inChatrooms.add(chatroom);
    }


    public void onLeaveChatroom(IChatroom chatroom){
        inChatrooms.remove(chatroom);
    }



    public List<IChatroom> getCurrentChatrooms(){
        return new ArrayList<IChatroom>(inChatrooms);
    }

    public boolean isInRoom(IChatroom chatroom){
        return inChatrooms.contains(chatroom);
    }

    public void setGroupId(String groupId){
        this.groupId = groupId;
    }

    public String getGroupId(){
        return this.groupId;
    }

}
