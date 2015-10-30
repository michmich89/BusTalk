package com.busgen.bustalk.server.message;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.user.IUser;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// TODO: Make sure disconnected users don't throw NullPointerException

/**
 * This class is responsible for sending messages to the clients, acts like a post office.
 */
public class BusTalkSender {

    private static final Logger LOGGER = Logger.getLogger(BusTalkSender.class.getName());
    private final UserHandler userHandler;

    private static class Holder {
        static final BusTalkSender INSTANCE = new BusTalkSender();
    }

    public static BusTalkSender getInstance(){
        return Holder.INSTANCE;
    }

    private BusTalkSender() {
        this.userHandler = UserHandler.getInstance();
    }

    /**
     * Sends a notification to all affected users when a chatroom is created
     *
     * @param user The user that created the chatroom
     * @param chatroom The newly created chatroom you want to notify users about
     */
    public void chatroomCreatedNotification(IUser user, List<IUser> users, IChatroom chatroom) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_CREATED_NOTIFICATION);
        jsonObject.put("title", chatroom.getTitle());
        jsonObject.put("chatId", chatroom.getIdNbr());
        jsonObject.put("isYours", false);

        // TODO: Could perhaps be more optimized... (maybe by creating objects representing groups that hold users and
        // chat rooms)
        for(IUser u : users){
            if(!u.equals(user)){
                userHandler.getSession(u).getAsyncRemote().sendObject(new UserMessage(jsonObject));
            }
        }
        jsonObject.put("isYours", true);
        userHandler.getSession(user).getAsyncRemote().sendObject(new UserMessage(jsonObject));
    }

    /**
     * Sends out a notification to all users in a charoom, when a new user joins
     *
     * @param user The user that just joined
     * @param chatroom The chatroom whos users will be notified
     */
    public void userJoinedNotification(IUser user, List<IUser> users, int chatId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.NEW_USER_IN_CHAT_NOTIFICATION);
        jsonObject.put("chatId", chatId);
        jsonObject.put("name", user.getName());
        jsonObject.put("interests", user.getInterests());

        for (IUser u : users) {
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
    public void userLeftNotification(IUser user, List<IUser> users, int chatId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.USER_LEFT_ROOM_NOTIFICATION);
        jsonObject.put("chatId", chatId);
        jsonObject.put("name", user.getName());

        for (IUser u : users) {
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
    public void chatDeletedNotification(List<IUser> users, int chatId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_DELETED_NOTIFICATION);
        jsonObject.put("chatId", chatId);

        for (IUser u : users) {
            userHandler.getSession(u).getAsyncRemote().sendObject(new UserMessage(jsonObject));
        }
    }

    /**
     * Sends out a message containing all chatrooms that a specific user can join
     *
     * @param user
     */
    public void listOfChatrooms(IUser user, List<IChatroom> chatrooms, String groupId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.LIST_OF_CHATROOMS_NOTIFICATION);
        jsonObject.put("groupId", groupId);

        for (IChatroom c : chatrooms) {
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
    public void listOfUsersInRoom(IUser user, List<IUser> users, int chatId) {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", MessageType.LIST_OF_USERS_IN_CHAT_NOTIFICATION);
        jsonObject.put("chatId", chatId);

        for (IUser u : users) {
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
    public void chatMessage(IUser sender, List<IUser> users, int chatId, String message) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.CHAT_MESSAGE_NOTIFICATION);
        jsonObject.put("chatId", chatId);
        jsonObject.put("sender", sender.getName());
        jsonObject.put("message", message);
        jsonObject.put("time", new Date().toString());
        jsonObject.put("isMe", false);

        for (IUser u : users) {
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
