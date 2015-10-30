package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.message.BusTalkSender;
import com.busgen.bustalk.server.chatroom.ChatIdNbrFullException;
import com.busgen.bustalk.server.chatroom.ChatroomFactory;
import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.user.IUser;
import com.busgen.bustalk.server.util.Constants;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    public boolean isUserInGroup(IUser user) {
        return this.users.contains(user);
    }

    public void joinGroup(IUser user) {
        this.users.add(user);
        this.userToChatrooms.put(user, new ArrayList<IChatroom>());
        LOGGER.log(Level.INFO, String.format("[{0}] Joined group \"{1}\"."),
                new Object[]{user.getName(), this.groupId});
    }

    public void leaveGroup(IUser user) {
        this.users.remove(user);

        List<IChatroom> userChatrooms = new ArrayList<IChatroom>(userToChatrooms.get(user));
        for (IChatroom chatroom : userChatrooms) {
            leaveRoom(user, chatroom.getIdNbr());
        }

        LOGGER.log(Level.INFO, String.format("[{0}] Left group \"{1}\"."),
                new Object[]{user.getName(), this.groupId});
    }

    public boolean isEmpty() {
        return this.users.isEmpty();
    }

    public boolean userIsInChatroom(IUser user, int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);
        List<IUser> users = chatroomToUsers.get(chatroom);

        return users.contains(user);
    }

    public void createRoom(IUser user, String title) {
        IChatroom chatroom = chatroomFactory.createChatroom(title);
        this.chatrooms.put(chatroom.getIdNbr(), chatroom);
        this.chatroomToUsers.put(chatroom, new ArrayList<IUser>());

        busTalkSender.chatroomCreatedNotification(user, this.users, chatroom);
        LOGGER.log(Level.INFO, String.format("[{0}] Created room \"{1}\"."),
                new Object[]{user.getName(), title});

        joinRoom(user, chatroom.getIdNbr());
    }

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

    public List<IChatroom> getChatrooms() {
        System.out.println(new ArrayList<IChatroom>(this.chatrooms.values()).toString());
        return new ArrayList<IChatroom>(this.chatrooms.values());
    }

    public List<IUser> getUsersInRoom(int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);
        return chatroomToUsers.get(chatroom);
    }

    public String getGroupId() {
        return this.groupId;
    }

    public List<IUser> getUsers() {
        return this.users;
    }

    public List<IChatroom> getRoomsForUser(IUser user) {
        return userToChatrooms.get(user);
    }

    private void removeRoom(IChatroom chatroom) {
        this.chatrooms.remove(chatroom);
        this.chatroomToUsers.remove(chatroom);

        LOGGER.log(Level.INFO, String.format("Chat room \"{1}\" with id \"{2}\" was deleted."),
                new Object[]{chatroom.getTitle(), chatrooms.inverse().get(chatroom)});
        busTalkSender.chatDeletedNotification(users, chatroom.getIdNbr());
    }
}
