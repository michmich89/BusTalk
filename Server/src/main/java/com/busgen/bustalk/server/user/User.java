package com.busgen.bustalk.server.user;

import com.busgen.bustalk.server.chatroom.Chatroom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristoffer on 2015-09-29.
 */
public class User implements IUser {

    private String name;
    private String interests;
    private final List<Chatroom> inChatrooms;
    private String groupId;

    public User(String name, String interests){
        this.name = name;
        this.interests = interests;
        inChatrooms = new ArrayList<Chatroom>();
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

    public void onJoinChatroom(Chatroom chatroom){
        inChatrooms.add(chatroom);
    }


    public void onLeaveChatroom(Chatroom chatroom){
        inChatrooms.remove(chatroom);
    }



    public List<Chatroom> getCurrentChatrooms(){
        return new ArrayList<Chatroom>(inChatrooms);
    }

    public boolean isInRoom(Chatroom chatroom){
        return inChatrooms.contains(chatroom);
    }

    public void setGroupId(String groupId){
        this.groupId = groupId;
    }

    public String getGroupId(){
        return this.groupId;
    }

}
