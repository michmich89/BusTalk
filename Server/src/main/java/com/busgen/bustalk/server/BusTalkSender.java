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


/**
 * This class handles all messages between users, acts like a post office.
 */
public class BusTalkSender {

    private final UserHandler userHandler;
    private final ChatroomHandler chatroomHandler;
    private static final Logger LOGGER = Logger.getLogger(BusTalkSender.class.getName());



    public BusTalkSender() {
        this.userHandler = UserHandler.getInstance();
        this.chatroomHandler = ChatroomHandler.getInstance();
    }

    /**
     * Sends a notification to all affected users when a chatroom is created
     *
     * @param user The user that created the chatroom
     * @param chatroom The newly created chatroom you want to notify users about
     */
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

    /**
     * Sends out a notification to all users in a charoom, when a new user joins
     *
     * @param user The user that just joined
     * @param chatroom The chatroom whos users will be notified
     */
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

    /**
     * Sends out a notification to all users in a chatroom, when a user has left the room
     *
     * @param user The user who left
     * @param chatroom The chatroom that the user left
     */
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

    /**
     * Sends out a message to affected users that a chatroom has been deleted.
     *
     * @param groupId the group ID of all affected users
     * @param chatroom the chatroom that was deleted
     */
    public void chatDeletedNotification(String groupId, Chatroom chatroom) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_DELETED_NOTIFICATION);
        jsonObject.put("chatId", chatroom.getIdNbr());

        for (User u : userHandler.getUsers()) {
            if(u.getGroupId().equals(groupId)) {
                userHandler.getSession(u).getAsyncRemote().sendObject(jsonObject);
            }
        }
    }

    /**
     * Sends out a message containing all chatrooms that a specific user can join
     *
     * @param user
     */
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

    /**
     * Sends out a message to a user, containing information of other users in a specific room
     *
     * @param user the user the message should be sent to
     * @param chatroom the chatroom the user wants info about
     */
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

    /**
     * When a user sends a message, this will send out that message to all users in a chatroom
     *
     * @param sender The user that sent the message
     * @param chatroom The chatroom the message was sent to
     * @param message The actual message the user wrote
     */
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