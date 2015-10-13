package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import com.busgen.bustalk.server.util.Constants;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BusTalkSender {

    private final UserHandler userHandler;
    private final ChatroomHandler chatroomHandler;
    private static final Logger LOGGER = Logger.getLogger(BusTalkSender.class.getName());



    public BusTalkSender() {
        this.userHandler = UserHandler.getInstance();
        this.chatroomHandler = ChatroomHandler.getInstance();
    }

    public void chatroomCreatedNotification(User user, Chatroom chatroom) {
        Session creator = userHandler.getSession(user);
        String groupId = user.getGroupId();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_CREATED_NOTIFICATION);
        jsonObject.put("title", chatroom.getTitle());
        jsonObject.put("chatId", chatroom.getIdNbr());
        jsonObject.put("isYours", false);

        for(User u : userHandler.getUsers()){
            if(!u.equals(user) && u.getGroupId().equals(groupId)){
                userHandler.getSession(u).getAsyncRemote().sendObject(jsonObject);
            }
        }
        jsonObject.put("isYours", true);
        creator.getAsyncRemote().sendObject(jsonObject);
    }

    public void userJoinedNotification(User user, Chatroom chatroom) {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("type", MessageType.NEW_USER_IN_CHAT_NOTIFICATION);
        objectToSend.put("chatId", chatroom.getIdNbr());
        objectToSend.put("name", user.getName());
        objectToSend.put("interests", user.getInterests());

        for (User u : chatroom.getChatroomUsers()) {
            Session s = userHandler.getSession(u);
            s.getAsyncRemote().sendObject(objectToSend);
        }
    }

    public void userLeftNotification(User user, Chatroom chatroom) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.USER_LEFT_ROOM_NOTIFICATION);
        jsonObject.put("chatId", chatroom.getIdNbr());
        jsonObject.put("name", user.getName());

        for (User u : chatroom.getChatroomUsers()) {
            Session s = userHandler.getSession(u);
            s.getAsyncRemote().sendObject(jsonObject);
        }
    }

    public void chatDeletedNotification(Chatroom chatroom) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_DELETED_NOTIFICATION);
        jsonObject.put("chatId", chatroom.getIdNbr());

        for (Session s : userHandler.getSessions()) {
            s.getAsyncRemote().sendObject(jsonObject);
        }
    }

    public void listOfChatrooms(User user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.LIST_OF_CHATROOMS_NOTIFICATION);

        List<Chatroom> tempChatroomList = chatroomHandler.getGroupOfChatrooms(user.getGroupId());
        for (Chatroom c : tempChatroomList) {
            JSONObject jsonChatroom = new JSONObject();
            jsonChatroom.put("chatId", c.getIdNbr());
            jsonChatroom.put("name", c.getTitle());
            jsonObject.append("chatrooms", jsonChatroom);
        }

        Session requester = userHandler.getSession(user);
        requester.getAsyncRemote().sendObject(jsonObject);

        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Sent list of chatrooms"),
                new Object[]{requester.getId(), user.getName()});
    }

    public void listOfUsersInRoom(User user, Chatroom chatroom) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", MessageType.LIST_OF_USERS_IN_CHAT_NOTIFICATION);
        for (User u : chatroomHandler.getListOfUsersInChatroom(chatroom.getIdNbr())) {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("name", u.getName());
            jsonUser.put("interests", u.getInterests());

            jsonObject.append("users", jsonUser);
        }

        Session requester = userHandler.getSession(user);
        requester.getAsyncRemote().sendObject(jsonObject);
    }

    public void chatMessage(User sender, Chatroom chatroom, String message) {

        int chatId = chatroom.getIdNbr();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.CHAT_MESSAGE_NOTIFICATION);
        jsonObject.put("chatId", chatId);
        jsonObject.put("sender", sender.getName());
        jsonObject.put("message", message);
        jsonObject.put("time", new Date().toString());

        for (User u : chatroomHandler.getListOfUsersInChatroom(chatId)) {
            Session s = userHandler.getSession(u);
            s.getAsyncRemote().sendObject(jsonObject);
        }
    }
}
