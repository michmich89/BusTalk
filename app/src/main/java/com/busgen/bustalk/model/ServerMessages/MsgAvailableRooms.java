package com.busgen.bustalk.model.ServerMessages;

import android.util.Log;

import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.IServerMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgAvailableRooms implements IServerMessage{
    private List<IChatroom> roomList;
    private String groupId;

    public MsgAvailableRooms(String groupId){
        roomList = new ArrayList<IChatroom>();
        this.groupId = groupId;
    }

    public List<IChatroom> getRoomList(){
        return roomList;
    }

    public void addRoomToList(IChatroom chatroom){
        roomList.add(chatroom);
    }

    public String getGroupId(){
        return groupId;
    }
}
