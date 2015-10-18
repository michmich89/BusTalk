package com.busgen.bustalk.model;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
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

    //Singleton pattern
    private static Client client = null;

    private Client(IUser user) {
        this.user = user;
        chatrooms = new ArrayList<IChatroom>();
    }

    public static Client getInstance() {
        if (client == null) {
            client = new Client(new User());
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
        user.setUserName(userName);
    }

    @Override
    public void setInterest(String interest) {
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

    @Override
    public void onEvent(Event event) {

        IServerMessage message = event.getMessage();

        if (event instanceof ToClientEvent) {
            if (message instanceof MsgChatMessage) {

            } else if (message instanceof MsgChooseNickname) {
                setUserName(((MsgChooseNickname) message).getNickname());
                System.out.println("client får ett event om choose nickname");
               IServerMessage serverMessage = new MsgChooseNickname(getUserName(), getInterest());
                Event testEvent = new ToActivityEvent(serverMessage);
                

                eventBus.postEvent(testEvent);

            } else if (message instanceof MsgCreateRoom) {

            } else if (message instanceof MsgJoinRoom) {

            } else if (message instanceof MsgLeaveRoom) {

            } else if (message instanceof MsgLostChatRoom) {

            } else if (message instanceof MsgLostUserInChat) {

            } else if (message instanceof MsgNewChatRoom) {

            } else if (message instanceof MsgNewUserInChat) {

            } else if (message instanceof MsgNicknameAvailable) {
            }
        }
    }

}
