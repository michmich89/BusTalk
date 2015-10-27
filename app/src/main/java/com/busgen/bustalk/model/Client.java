package com.busgen.bustalk.model;

import android.util.Log;

import com.busgen.bustalk.MainChatActivity;
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
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
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
        if (chatroom != null && !chatrooms.contains(chatroom)) {
            chatrooms.add(chatroom);
        }
    }

    @Override
    public void leaveRoom(IChatroom chatroom) {
        if (chatroom != null && chatrooms.contains(chatroom)) {
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

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

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
                if (!chatMessage.getNickname().equals(getUserName())) {
                    chatrooms.get(chatMessage.getChatId()).addMessage(chatMessage);
                    Event newEvent = new ToActivityEvent(chatMessage);
                    eventBus.postEvent(newEvent);
                }
            } else if (message instanceof MsgChooseNickname) {
                /*
                sets username and alerts activities about it (Not used atm)
                 */
                Log.d("MyTag", "choosing nickname in Client event response");
                setUserName(((MsgChooseNickname) message).getNickname());
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);
            } else if (message instanceof MsgCreateRoom) {

            } else if (message instanceof MsgJoinRoom) {
                IChatroom chatroom = ((MsgJoinRoom) message).getChatroom();

                //Skala bort kod här sedan, joinRoom innehåller det mesta
                if (!chatrooms.contains(chatroom)) {
                    joinRoom(chatroom);
                    Event newEvent = new ToActivityEvent(message);
                    eventBus.postEvent(newEvent);
                }

            } else if (message instanceof MsgLeaveRoom) {
                int chatId = ((MsgLeaveRoom) message).getChatID();

                /** Check if client is connected to the room and if so remove it from list**/
                for (IChatroom c : chatrooms) {
                    if (c.getChatID() == chatId) {
                        leaveRoom(c);
                        Event newEvent = new ToActivityEvent(message);
                        eventBus.postEvent(newEvent);
                    }
                }

            } else if (message instanceof MsgLostChatRoom) {
                int chatId = ((MsgLostChatRoom) message).getChatID();

                for (IChatroom c : chatrooms) {
                    if (c.getChatID() == chatId) {
                        this.chatrooms.remove(c);
                        Event newEvent = new ToActivityEvent(message);
                        eventBus.postEvent(newEvent);
                    }
                }

            } else if (message instanceof MsgLostUserInChat) {

                int chatId = ((MsgLostUserInChat) message).getChatID();
                IUser user = ((MsgLostUserInChat) message).getUser();

                /** Check if user is me and if so leave room (remove room from local list of rooms)
                 * Could be extracted to separate method. **/

                for (IChatroom c : chatrooms) {
                    if (c.getChatID() == chatId) {
                        if (user.equals(this.user)) {
                            leaveRoom(c);
                        } else {
                            c.removeUser(user);
                        }
                        Event newEvent = new ToActivityEvent(message);
                        eventBus.postEvent(newEvent);
                    }
                }


            } else if (message instanceof MsgNewChatRoom) {
            } else if (message instanceof MsgNewUserInChat) {
                int chatId = ((MsgNewUserInChat) message).getChatID();
                IUser user = ((MsgNewUserInChat) message).getUser();

                /** Check if user is me and if so join room (add room to local list of rooms)
                 * if it's somebody else, add them as user to local chatroom
                 * Could be extracted to separate methods. **/

                if (user.equals(this.user)) {
                    IChatroom chatroom = new Chatroom(chatId, "");
                    chatrooms.add(chatroom);
                } else if (user != null){
                   for (IChatroom c : chatrooms) {
                       if (c.getChatID() == chatId) {
                               c.addUser(user);
                           }
                       }
                   }
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);

                //for (IChatroom c : chatrooms) {
                //    if (c.getChatID() == chatId) {
                //        if (user.equals(this.user)) {
                //            joinRoom(c);
                //        } else {
                //            c.addUser(user);
                //        }
                //        Event newEvent = new ToActivityEvent(message);
                //        eventBus.postEvent(newEvent);
                //    }
                //}

            } else if (message instanceof MsgNicknameAvailable) {
                Log.d("MyTag", "Sending availability info to activity");
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);
            } else if (message instanceof MsgAvailableRooms) {
                // chatrooms = ((MsgAvailableRooms) message).getRoomList();
                Event newEvent = new ToActivityEvent(message);
                eventBus.postEvent(newEvent);
            } else if (message instanceof MsgPlatformDataRequest) {
            }
        }
    }
}

