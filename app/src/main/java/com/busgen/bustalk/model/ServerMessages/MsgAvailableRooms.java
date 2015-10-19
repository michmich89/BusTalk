package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.IServerMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgAvailableRooms implements IServerMessage{
    private ArrayList<Chatroom> roomList;
    private String groupId;

    public MsgAvailableRooms(String groupId){
        roomList = new ArrayList<Chatroom>();
        this.groupId = groupId;
    }

    public ArrayList<Chatroom> getRoomList(){
        return roomList;
    }

    public void addRoomToList(Chatroom chatroom){
        roomList.add(chatroom);
    }

    public String getGroupId(){
        return groupId;
    }
}
