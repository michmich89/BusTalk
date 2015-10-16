package com.busgen.bustalk.model.ServerMessages;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgAvailableRooms {
    private List<MsgNewChatRoom> roomList;

    public MsgAvailableRooms(){
        roomList = new ArrayList<MsgNewChatRoom>();
    }

    public List<MsgNewChatRoom> getRoomList(){
        return roomList;
    }

    public void addMsgRoomToList(MsgNewChatRoom message){
        roomList.add(message);
    }
}
