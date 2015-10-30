package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.chatroom.ChatIdNbrFullException;
import com.busgen.bustalk.server.chatroom.ChatroomFactory;
import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.message.BusTalkSender;
import com.busgen.bustalk.server.user.IUser;
import com.busgen.bustalk.server.util.Constants;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles all the logic and data that has to do with a group of chat rooms, such what chat rooms a user is
 * in, make a user join a room if it can, etc.
 */
public class ChatGroup implements IChatGroup {
    private final BiMap<Integer, IChatroom> chatrooms;
    private final List<IUser> users;
    private final Map<IUser, List<IChatroom>> userToChatrooms;
    private final Map<IChatroom, List<IUser>> chatroomToUsers;
    private final ChatroomFactory chatroomFactory;
    private final String groupId;
    private static final Logger LOGGER = Logger.getLogger(ChatGroup.class.getName());
    private final BusTalkSender busTalkSender;

    public ChatGroup(String groupId) {
        this.chatrooms = Maps.synchronizedBiMap(HashBiMap.<Integer, IChatroom>create());
        this.users = Collections.synchronizedList(new ArrayList<IUser>());
        this.userToChatrooms = Collections.synchronizedMap(new HashMap<IUser, List<IChatroom>>());
        this.chatroomToUsers = Collections.synchronizedMap(new HashMap<IChatroom, List<IUser>>());
        this.chatroomFactory = ChatroomFactory.getFactory();
        this.busTalkSender = BusTalkSender.getInstance();
        this.groupId = groupId;

        try {
            IChatroom chatroom = chatroomFactory.createMainChatroom(this.groupId);
            chatrooms.put(chatroom.getIdNbr(), chatroom);
            chatroomToUsers.put(chatroom, new ArrayList<IUser>());
        } catch (ChatIdNbrFullException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a specific user is in this group.
     * @param user The user which is to be checked.
     * @return True if user is in group, false otherwise.
     */
    public boolean isUserInGroup(IUser user) {
        return this.users.contains(user);
    }

    /**
     * Joins this group with a specific user.
     * @param user The user which is to join the group.
     */
    public void joinGroup(IUser user) {
        this.users.add(user);
        this.userToChatrooms.put(user, new ArrayList<IChatroom>());
        LOGGER.log(Level.INFO, String.format("[{0}] Joined group \"{1}\"."),
                new Object[]{user.getName(), this.groupId});
    }

    /**
     * Leaves this group with a specific user.
     * @param user The user which is to leave this group.
     */
    public void leaveGroup(IUser user) {
        this.users.remove(user);

        List<IChatroom> userChatrooms = new ArrayList<IChatroom>(userToChatrooms.get(user));
        for (IChatroom chatroom : userChatrooms) {
            leaveRoom(user, chatroom.getIdNbr());
        }

        LOGGER.log(Level.INFO, String.format("[{0}] Left group \"{1}\"."),
                new Object[]{user.getName(), this.groupId});
    }

    /**
     * Checks if there are any users in this group.
     * @return True if there are no users, false if there are users in the room.
     */
    public boolean isEmpty() {
        return this.users.isEmpty();
    }

    /**
     * Check if a specific user is in a specific chat room.
     * @param user The user which is to be checked.
     * @param chatId The id of the chat that may or may not contain the user.
     * @return True if the user is in the chat, false otherwise.
     */
    public boolean userIsInChatroom(IUser user, int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);
        List<IUser> users = chatroomToUsers.get(chatroom);

        return users.contains(user);
    }

    /**
     * Creates a chat room and joins the room with the user that created the room.
     * @param user The user that creates the room.
     * @param title The title of the room.
     */
    public void createRoom(IUser user, String title) {
        IChatroom chatroom = chatroomFactory.createChatroom(title);
        this.chatrooms.put(chatroom.getIdNbr(), chatroom);
        this.chatroomToUsers.put(chatroom, new ArrayList<IUser>());

        busTalkSender.chatroomCreatedNotification(user, this.users, chatroom);
        LOGGER.log(Level.INFO, String.format("[{0}] Created room \"{1}\"."),
                new Object[]{user.getName(), title});

        joinRoom(user, chatroom.getIdNbr());
    }

    /**
     * Joins a room with specific user.
     * @param user The user which is to join a room.
     * @param chatId The id of the chat which is to be joined.
     */
    public void joinRoom(IUser user, int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);

        // Make sure chat room is in this group
        if (chatroom == null) {
            LOGGER.log(Level.INFO, String.format("[{0}] Tried to join chat room with id \"{1}\", but that chat " +
                            "room does not exist in the group the user is in."),
                    new Object[]{user.getName(), chatId});
            return;
        // Make sure user isn't already in chat room
        } else if (chatroomToUsers.get(chatroom).contains(user)){
            LOGGER.log(Level.INFO, String.format("[{0}] Tried to join chat room \"{1}\" with id \"{2}\", but user is " +
                            "already in room."),
                    new Object[]{user.getName(), chatroom.getTitle(), chatId});
            return;
        }

        // Add the chat room to the user's list of chat rooms
        List<IChatroom> chatrooms = this.userToChatrooms.get(user);
        chatrooms.add(chatroom);

        // Add the user to the chat room's list of users
        List<IUser> users = this.chatroomToUsers.get(chatroom);
        users.add(user);

        LOGGER.log(Level.INFO, String.format("[{0}] Joined chat room \"{1}\" with id \"{2}\"."),
                new Object[]{user.getName(), chatroom.getTitle(), chatId});
        busTalkSender.userJoinedNotification(user, chatroomToUsers.get(chatroom), chatId);
    }

    /**
     * Leaves a room with a specific user.
     * @param user The user which is to leave a room.
     * @param chatId The id of the chat which is to be left.
     */
    public void leaveRoom(IUser user, int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);

        if (chatroom == null) {
            LOGGER.log(Level.INFO, String.format("[{0}] Tried to leave chat room with id \"{1}\", but that chat " +
                            "room does not exist in the group the user is in."),
                    new Object[]{user.getName(), chatId});
            return;
        } else if (!getUsersInRoom(chatId).contains(user)){
            LOGGER.log(Level.INFO, String.format("[{0}] Tried to leave chat room \"{1}\" with id \"{2}\", but user is " +
                            "not in room."),
                    new Object[]{user.getName(), chatroom.getTitle(), chatId});
            return;
        }

        this.userToChatrooms.get(user).remove(chatroom);
        this.chatroomToUsers.get(chatroom).remove(user);

        LOGGER.log(Level.INFO, String.format("[{0}] Left chat room \"{1}\" with id \"{2}\"."),
                new Object[]{user.getName(), chatroom.getTitle(), chatId});
        busTalkSender.userLeftNotification(user, chatroomToUsers.get(chatroom), chatId);

        if (chatroomToUsers.get(chatroom).isEmpty() && chatroom.getIdNbr() >= Constants.NBR_OF_RESERVED_CHAT_IDS) {
            removeRoom(chatroom);
        }
    }

    /**
     * @return A list of chat rooms in this group.
     */
    public List<IChatroom> getChatrooms() {
        System.out.println(new ArrayList<IChatroom>(this.chatrooms.values()).toString());
        return new ArrayList<IChatroom>(this.chatrooms.values());
    }

    /**
     * @param chatId The id of the chat that the users are gotten from.
     * @return A list of users in the specified room.
     */
    public List<IUser> getUsersInRoom(int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);
        return chatroomToUsers.get(chatroom);
    }

    /**
     * @return The id of this group.
     */
    public String getGroupId() {
        return this.groupId;
    }

    /**
     * @return A list of the users in this group.
     */
    public List<IUser> getUsers() {
        return this.users;
    }

    /**
     * Returns the room a specific user is in.
     * @param user The user which the chat rooms are being gotten for.
     * @return A list of chat rooms the specified user is in.
     */
    public List<IChatroom> getRoomsForUser(IUser user) {
        return userToChatrooms.get(user);
    }

    private void removeRoom(IChatroom chatroom) {
        this.chatrooms.remove(chatroom);
        this.chatroomToUsers.remove(chatroom);
        this.chatrooms.remove(chatroom.getIdNbr());

        LOGGER.log(Level.INFO, String.format("Chat room \"{1}\" with id \"{2}\" was deleted."),
                new Object[]{chatroom.getTitle(), chatrooms.inverse().get(chatroom)});
        busTalkSender.chatDeletedNotification(users, chatroom.getIdNbr());
    }
}
