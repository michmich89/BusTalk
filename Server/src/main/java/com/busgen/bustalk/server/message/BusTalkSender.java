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


    /**
     * Getter for a singleton method using the Initialization-on-demand-holder idiom.
     * Ensures thread safety
     *
     * @return
     */
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
     * Sends out a notification to all affected users in a chatroom that a new user has joined.
     *
     * @param user the new User in the room
     * @param users the list of of Users in the chatroom
     * @param chatId The ID of the affected chatroom
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
     * Sends out a notification that a user has left a chatroom
     *
     * @param user The user that left
     * @param users The user in the affected chatroom
     * @param chatId The ID number of the affected chatroom
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
     * Sends out a notification that a chatroom has been deleted
     *
     * @param users Users in the affected group
     * @param chatId the ID of the deleted chatroom
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
     * Sends out a list of chatrooms to a specific user
     *
     * @param user the User who requested a list of available chatrooms
     * @param chatrooms The list of chatrooms that is available to the User
     * @param groupId the ID of the group affected
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
     * Sends out a list of users in a chatroom to a specific user
     *
     * @param user The user who requested a list
     * @param users the list of users in the affected chatroom
     * @param chatId the ID of that affecetd chatroom
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
     * Sends out a notification with a message when a user is sending a Bustalkmessage
     *
     * @param sender the user who sent a message
     * @param users the user who should receive the message (Those in the chatroom)
     * @param chatId the ID of the chatroom the message was sent to
     * @param message The actual message
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
        // TODO: Send info to all clients that a user has changed name
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.NAME_AND_INTEREST_SET);
        jsonObject.put("succeeded", succeeded);

        session.getAsyncRemote().sendObject(new UserMessage(jsonObject));
    }
}
