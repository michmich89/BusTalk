package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.Chatroom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgAvailableRooms {
    private List<Chatroom> roomList;
    private String groupId;

    public MsgAvailableRooms(String groupId){
        roomList = new ArrayList<Chatroom>();
        this.groupId = groupId;
    }

    public List<Chatroom> getRoomList(){
        return roomList;
    }

    public void addRoomToList(Chatroom chatroom){
        roomList.add(chatroom);
    }

    public String getGroupId(){
        return groupId;
    }
}
