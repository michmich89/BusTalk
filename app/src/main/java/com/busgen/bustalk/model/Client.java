package com.busgen.bustalk.model;

import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.model.ServerMessages.MsgAvailableRooms;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;
import com.busgen.bustalk.service.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Johan on 2015-10-05.
 */
public class Client implements IClient, IEventBusListener {

    private IUser user;
    private List<IChatroom> chatrooms;
    private EventBus eventBus;
    private String groupId;

    public Client(){
        this.user = new User();
        eventBus = EventBus.getInstance();
        groupId = "1";

    }

    @Override
    public String getUserName() {
        return user.getUserName();
    }

    @Override
    public String getInterest() {
        return user.getInterest();
    }

    @Override
    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public void setUserName(String userName) {
        Log.d("MyTag", "inside setusername");
        user.setUserName(userName);
    }

    @Override
    public void setInterest(String interest) {
        Log.d("MyTag", "inside setinterest");
        user.setInterest(interest);
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Collection<IChatroom> getChatrooms() {
        return chatrooms;
    }

    @Override
    public Collection<IServerMessage> recieveMessages() {
        return null;
    }

    @Override
    public void sendMessage(IServerMessage serverMessage) {

    }

    @Override
    public void joinRoom(IChatroom chatroom) {
        chatrooms.add(chatroom);
    }

    @Override
    public void leaveRoom(IChatroom chatroom) {
        if (chatrooms.contains(chatroom)) {
            chatrooms.remove(chatroom);
        }
    }

    @Override
    public void addMessageToChatroom(MsgChatMessage message) {
        for (int i = 0; i < chatrooms.size(); i++) {
            if (chatrooms.get(i).getChatID() == message.getChatId()) {
                chatrooms.get(i).addMessage(message);
            }
        }
    }

    public void setGroupId(String groupId){
        this.groupId = groupId;
    }

    public String getGroupId(){
        return groupId;
    }

    @Override
    public void onEvent(Event event) {

        IServerMessage message = event.getMessage();

        if (event instanceof ToClientEvent) {
            Log.d("MyTag", "Client received some sort of event");
            Log.d("MyTag", "message type: ");
            if (message instanceof MsgChatMessage) {


            } else if(message == null){
                Log.d("MyTag", "null message");
            }

            else if (message instanceof MsgChooseNickname) {
                Log.d("MyTag", "1");
                /*
                sets username and alerts activities about it
                 */
                setUserName(((MsgChooseNickname) message).getNickname());
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);

            } else if (message instanceof MsgCreateRoom) {
                Log.d("MyTag", "2");
            } else if (message instanceof MsgJoinRoom) {
                Log.d("MyTag", "3");
                IChatroom chatroom = ((MsgJoinRoom) message).getChatroom();
                chatrooms.add(chatroom);

            } else if (message instanceof MsgLeaveRoom) {
                Log.d("MyTag", "4");
            } else if (message instanceof MsgLostChatRoom) {
                Log.d("MyTag", "5");
            } else if (message instanceof MsgLostUserInChat) {
                Log.d("MyTag", "6");
            } else if (message instanceof MsgNewChatRoom) {
                Log.d("MyTag", "7");
            } else if (message instanceof MsgNewUserInChat) {
                Log.d("MyTag", "8");
            } else if (message instanceof MsgNicknameAvailable) {
                Log.d("MyTag", "9");
                Log.d("MyTag", "Sending availability info to activity");
                Event newEvent = new ToActivityEvent(message);

                eventBus.postEvent(newEvent);

            } else if (message instanceof MsgAvailableRooms) {
                chatrooms = ((MsgAvailableRooms) message).getRoomList();
                Log.d("MyTag", "chatrooms blabla");
                //Log.d("MyTag", "" + chatrooms.size());
            }
        }
    }

}
