package com.busgen.bustalk.model;

import android.util.Log;

import com.busgen.bustalk.MainChatActivity;
import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
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
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
import com.busgen.bustalk.model.ServerMessages.MsgSetGroupId;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChat;
import com.busgen.bustalk.model.ServerMessages.MsgUsersInChatRequest;
import com.busgen.bustalk.service.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Client implements IClient, IEventBusListener {

    private IUser user;
    private List<IChatroom> chatrooms;
    private EventBus eventBus;
    private String groupId;
    private String nextBusStop = "Nästa hållplats";

    private static Client client = null;

    private Client() {
        this.user = new User();
        eventBus = EventBus.getInstance();
        chatrooms = new ArrayList<IChatroom>();
        groupId = "1";
    }

    public static Client getInstance() {
        if (client == null) {
            client = new Client();
        }
        return client;
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
    public List<IChatroom> getChatrooms() {
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

        if (chatroom != null && !chatrooms.contains(chatroom)) {
            chatroom.addUser(user);
            chatrooms.add(chatroom);
            Log.d("MyTag", "Added chatroom " + chatroom.getChatID() + " to Client's chatrooms");
            Log.d("MyTag", "Client's chatroom size: " + chatrooms.size());
            Log.d("MyTag", "For good measure, chatroom nbr 0 is: " + getChatrooms().get(0).getChatID());
            requestUsersFromServer(chatroom.getChatID());
        }
    }

    /** Check if client is connected to the room and if so remove it from list**/
    @Override
    public void leaveRoom(IChatroom chatroom) {
        if (chatroom != null && chatrooms.contains(chatroom)) {
            chatrooms.remove(chatroom);

            MsgLeaveRoom msgLeaveRoom = new MsgLeaveRoom(chatroom.getChatID());
            Event newEvent = new ToActivityEvent(msgLeaveRoom);
            eventBus.postEvent(newEvent);
        }
    }

    /** Check if client is connected to the room and if so remove it from list**/
    @Override
    public void leaveRoom(int chatId) {
        for (IChatroom c : chatrooms) {
            if (c.getChatID() == chatId) {
                leaveRoom(c);
            }
        }
    }

    @Override
    public void setUsers(int chatId, List<IUser> userList){

        for (IChatroom c : chatrooms) {
            if (c.getChatID() == chatId) {
                c.setUsers(userList);
            }
        }

        MsgUsersInChat msgUsersInChat = new MsgUsersInChat(chatId);
        Event activityEvent = new ToActivityEvent(msgUsersInChat);
        eventBus.postEvent(activityEvent);
    }

    @Override
    public void addUser(int chatId, IUser user){
        for (IChatroom c : chatrooms) {
            if (c.getChatID() == chatId) {
                c.addUser(user);
                Log.d("MyTag", "Added user " + user.getUserName() + " to chatroom " + c.getChatID());
                Log.d("MyTag", "It now has " + c.getNbrOfUsers() + " users");
            }
        }
    }

    @Override
    public void removeUser(int chatId, IUser user){
        for (IChatroom c : chatrooms) {
            if (c.getChatID() == chatId) {
                if (user.equals(this.user)) {
                    leaveRoom(c);
                } else {
                    c.removeUser(user);
                }
                MsgLostUserInChat msgLostUserInChat = new MsgLostUserInChat(chatId, user);
                Event newEvent = new ToActivityEvent(msgLostUserInChat);
                eventBus.postEvent(newEvent);
            }
        }
    }

    @Override
    public void requestUsersFromServer(int chatId){
        MsgUsersInChatRequest userReqMsg = new MsgUsersInChatRequest(chatId);
        Event newEvent = new ToServerEvent(userReqMsg);
        eventBus.postEvent(newEvent);
    }

    @Override
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();
        if (event instanceof ToClientEvent) {
            Log.d("MyTag", "Client receiving some sort of event");
            Log.d("MyTag", message.getClass().getName());
            if (message instanceof MsgChatMessage) {
                MsgChatMessage chatMessage = (MsgChatMessage) message;
                Log.d("MyTag", "sender of message: " + chatMessage.getNickname());
                Log.d("MyTag", "And I am: " + getUserName());
                Log.d("MyTag", "Is it me who sent it? " + chatMessage.getNickname().equals(getUserName()));

                Event newEvent = new ToActivityEvent(chatMessage);
                eventBus.postEvent(newEvent);

            } else if (message instanceof MsgCreateRoom) {


            } else if (message instanceof MsgLeaveRoom) {
                MsgLeaveRoom msgLeaveRoom = ((MsgLeaveRoom) message);
                int chatId = msgLeaveRoom.getChatID();
                leaveRoom(chatId);

            } else if (message instanceof MsgLostChatRoom) {
                int chatId = ((MsgLostChatRoom) message).getChatID();
                leaveRoom(chatId);

            } else if (message instanceof MsgLostUserInChat) {
                int chatId = ((MsgLostUserInChat) message).getChatID();
                IUser user = ((MsgLostUserInChat) message).getUser();

                /** Check if user is me and if so leave room (remove room from local list of rooms)
                 * Could be extracted to separate method. **/
                removeUser(chatId, user);

            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
                int chatId = ((MsgNewUserInChat) message).getChatID();
                IUser user = ((MsgNewUserInChat) message).getUser();

                /** Check if user is me and if so join room (add room to local list of rooms)
                 * if it's somebody else, add them as user to local chatroom
                 * Could be extracted to separate methods. **/

                if (user.equals(this.user)) {
                    Log.d("MyTag", "It was me who joined a room, adding the room to my list");
                    IChatroom chatroom = new Chatroom(chatId, "");
                    joinRoom(chatroom);
                } else if (user != null){
                    Log.d("MyTag", "Someone joined a room, updating chatroom");
                    addUser(chatId, user);
                }
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);


            } else if (message instanceof MsgUsersInChat) {
                int chatId = ((MsgUsersInChat) message).getChatID();
                List<IUser> userList = ((MsgUsersInChat) message).getUserList();
                setUsers(chatId, userList);

            } else if (message instanceof MsgNicknameAvailable) {
                Log.d("MyTag", "Sending availability info to activity");
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);
            } else if (message instanceof MsgAvailableRooms) {
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);
            } else if (message instanceof MsgSetGroupId) {
                MsgSetGroupId groupIdMessage = (MsgSetGroupId)message;
                setGroupId(((MsgSetGroupId) message).getGroupId());
            }
        }
    }
}

