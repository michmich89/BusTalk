package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import com.busgen.bustalk.server.util.Constants;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

public class BusTalkSender {

    private final UserHandler userHandler;
    private final ChatroomHandler chatroomHandler;

    private static class Holder {
        static final BusTalkSender INSTANCE = new BusTalkSender();
    }

    private BusTalkSender() {
        this.userHandler = UserHandler.getInstance();
        this.chatroomHandler = ChatroomHandler.getInstance();
    }

    public static BusTalkSender getInstance() {
        return Holder.INSTANCE;
    }

    public void chatroomCreatedNotification(User user, Chatroom chatroom) {
        Session creator = userHandler.getSession(user);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_CREATED_NOTIFICATION);
        jsonObject.put("title", chatroom.getTitle());
        jsonObject.put("chatId", chatroom.getIdNbr());
        jsonObject.put("isYours", false);
        for (Session s : userHandler.getSessions()) {
            if(!s.equals(creator)) {
                s.getAsyncRemote().sendObject(jsonObject);
            }
        }
        jsonObject.put("isYours", true);
        creator.getAsyncRemote().sendObject(jsonObject);
    }

    public void userJoinedNotification(User user, Chatroom chatroom) {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("type", MessageType.NEW_USER_IN_CHAT_NOTIFICATION); //What notification should be sent back?
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

        Iterator iterator = chatroomHandler.getListOfOpenChatrooms().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            Chatroom chatroom = (Chatroom)pair.getValue();
            JSONObject jsonChatroom = new JSONObject();
            jsonChatroom.put("chatId", chatroom.getIdNbr());
            jsonChatroom.put("name", chatroom.getTitle());
            jsonObject.append("chatrooms", jsonChatroom);
        }

        Session requester = userHandler.getSession(user);
        requester.getAsyncRemote().sendObject(jsonObject);

        Constants.LOGGER.log(Level.INFO, String.format("[{0}:{1}] Sent list of chatrooms"),
                new Object[]{requester.getId(), user.getName()});
    }

    public void listOfUsersInRoom(User user, Chatroom chatroom) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", MessageType.LIST_OF_USERS_IN_CHAT_NOTIFICATION);
        for (User u : chatroomHandler.getListOfUsersInChatroom(chatroom.getIdNbr())) {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("name", user.getName());
            jsonUser.put("interests", user.getInterests());

            jsonObject.append("users", jsonUser);
        }

        Session requester = userHandler.getSession(user);
        requester.getAsyncRemote().sendObject(jsonObject);
    }
}
