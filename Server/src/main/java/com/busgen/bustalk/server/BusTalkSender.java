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

// TODO: Make sure disconnected users don't throw NullPointerException

/**
 * This class is responsible for sending messages to the clients, acts like a post office.
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

        // TODO: Could perhaps be more optimized...
        for(User u : userHandler.getUsers()){
            if(!u.equals(user) && u.getGroupId().equals(groupId)){
                userHandler.getSession(u).getAsyncRemote().sendObject(new UserMessage(jsonObject));
            }
        }
        jsonObject.put("isYours", true);
        creator.getAsyncRemote().sendObject(new UserMessage(jsonObject));
    }

    /**
     * Sends out a notification to all users in a charoom, when a new user joins
     *
     * @param user The user that just joined
     * @param chatroom The chatroom whos users will be notified
     */
    public void userJoinedNotification(User user, Chatroom chatroom) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.NEW_USER_IN_CHAT_NOTIFICATION);
        jsonObject.put("chatId", chatroom.getIdNbr());
        jsonObject.put("name", user.getName());
        jsonObject.put("interests", user.getInterests());

        for (User u : chatroom.getChatroomUsers()) {
            Session s = userHandler.getSession(u);
            s.getAsyncRemote().sendObject(new UserMessage(jsonObject));
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
            s.getAsyncRemote().sendObject(new UserMessage(jsonObject));
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
                userHandler.getSession(u).getAsyncRemote().sendObject(new UserMessage(jsonObject));
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
        jsonObject.put("groupId", user.getGroupId());

        List<Chatroom> tempChatroomList = chatroomHandler.getGroupOfChatrooms(user.getGroupId());
        for (Chatroom c : tempChatroomList) {
            JSONObject jsonChatroom = new JSONObject();
            jsonChatroom.put("chatId", c.getIdNbr());
            jsonChatroom.put("name", c.getTitle());
            jsonObject.append("chatrooms", jsonChatroom);
        }

        Session requester = userHandler.getSession(user);
        requester.getAsyncRemote().sendObject(new UserMessage(jsonObject));

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
        for (User u : chatroom.getChatroomUsers()) {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("name", u.getName());
            jsonUser.put("interests", u.getInterests());

            jsonObject.append("users", jsonUser);
        }

        Session requester = userHandler.getSession(user);
        requester.getAsyncRemote().sendObject(new UserMessage(jsonObject));
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
        jsonObject.put("isMe", false);


        System.out.println(chatroom.getChatroomUsers().toString());
        for (User u : chatroom.getChatroomUsers()) {
            if (!u.equals(sender)) {
                Session s = userHandler.getSession(u);
                s.getAsyncRemote().sendObject(new UserMessage(jsonObject));
            }
        }

        jsonObject.put("isMe", true);
        Session session = userHandler.getSession(sender);
        session.getAsyncRemote().sendObject(new UserMessage(jsonObject));
    }

    /**
     * Sends a message to the client requesting to set name/interests, informing them whether the name/interests were
     * successful or not
     * @param session The session that requested to set name/interests
     * @param succeeded true if the name/interests were successfully set, false otherwise
     */
    public void userNameAndInterestStatus(Session session, boolean succeeded) {
        // TODO: Is there a better way than having session as parameter here? User might have not been created yet
        // TODO: Send info to all clients that a user has changed name
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.NAME_AND_INTEREST_SET);
        jsonObject.put("succeeded", succeeded);

        session.getAsyncRemote().sendObject(new UserMessage(jsonObject));
    }
}
