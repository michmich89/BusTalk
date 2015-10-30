package com.busgen.bustalk;

import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.model.Chatroom;
import com.busgen.bustalk.model.Client;
import com.busgen.bustalk.model.IChatroom;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Johan on 2015-10-30.
 */
public class ClientTest extends TestCase {
    protected Client client;

    @Override
    public void setUp() {
        client = Client.getInstance();
    }

    public void testCanJoinAndLeaveRoom() {
        int chatId = 999;
        String title = "";
        Chatroom chatroom = new Chatroom(chatId, title);
        boolean joinedRoom = false;
        boolean leftRoom = false;
        String testChatMessage = "testChatMessage";
        String testNickname = "testNickname";
        String date = "testDate";
        MsgChatMessage chatMessage = new MsgChatMessage(true, testChatMessage, date, testNickname, chatId);

        client.getChatrooms().clear();
        client.joinRoom(chatroom);
        List<IChatroom> chatrooms = client.getChatrooms();

        if (chatrooms.contains(chatroom)) {
            joinedRoom = true;
//            testCanAddMessageToChatroom(chatroom, chatMessage);
        }

        if (joinedRoom == true) {
            client.leaveRoom(chatroom);
            if (!chatrooms.contains(chatroom)) {
                leftRoom = true;
            }
        }

        assertTrue(joinedRoom && leftRoom);
    }

//    public void testCanAddMessageToChatroom(Chatroom chatroom, MsgChatMessage message) {
//        boolean addedMessage = false;
//        chatroom.addMessage(message);
//
//        Log.d("MyTag", "testingadd");
//        if (chatroom.getMessages().contains(message)) {
//            addedMessage = true;
//        }
//
//        assertTrue(addedMessage);
//    }
}