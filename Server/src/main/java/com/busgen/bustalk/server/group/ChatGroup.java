package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.chatroom.ChatIdNbrFullException;
import com.busgen.bustalk.server.chatroom.ChatroomFactory;
import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.user.IUser;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.*;

public class ChatGroup implements IChatGroup {
    private final BiMap<Integer, IChatroom> chatrooms;
    private final List<IUser> users;
    private final Map<IUser, List<IChatroom>> userToChatrooms;
    private final Map<IChatroom, List<IUser>> chatroomToUsers;
    private final ChatroomFactory chatroomFactory;
    private final String groupId;

    public ChatGroup(String groupId) {
        this.chatrooms = Maps.synchronizedBiMap(HashBiMap.<Integer, IChatroom>create());
        this.users = Collections.synchronizedList(new ArrayList<IUser>());
        this.userToChatrooms = Collections.synchronizedMap(new HashMap<IUser, List<IChatroom>>());
        this.chatroomToUsers = Collections.synchronizedMap(new HashMap<IChatroom, List<IUser>>());
        this.chatroomFactory = ChatroomFactory.getFactory();
        this.groupId = groupId;

        try {
            IChatroom chatroom = chatroomFactory.createMainChatroom(this.groupId);
            chatrooms.put(chatroom.getIdNbr(), chatroom);
        } catch (ChatIdNbrFullException e) {
            e.printStackTrace();
        }
    }

    public boolean isUserInGroup(IUser user) {
        return this.users.contains(user);
    }

    public void joinGroup(IUser user) {
        this.users.add(user);
        System.out.println(user.getName() + " joined group " + this.groupId);
    }

    public void leaveGroup(IUser user) {
        this.users.remove(user);
        System.out.println(user.getName() + " left group " + this.groupId);
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

        System.out.println(user.getName() + " created room " + chatroom.getTitle());

        joinRoom(user, chatroom.getIdNbr());
    }

    public void joinRoom(IUser user, int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);

        if (chatroom == null) {
            return;
        }

        List<IChatroom> chatrooms = this.userToChatrooms.get(user);
        if (chatrooms == null) {
            chatrooms = new ArrayList<>();
            this.userToChatrooms.put(user, chatrooms);
        }
        chatrooms.add(chatroom);

        List<IUser> users = this.chatroomToUsers.get(user);
        if (users == null) {
            users = new ArrayList<>();
            this.chatroomToUsers.put(chatroom, users);
        }
        users.add(user);

        System.out.println(user.getName() + " joined room " + chatroom.getTitle());
    }

    public void leaveRoom(IUser user, int chatId) {
        IChatroom chatroom = chatrooms.get(chatId);

        if (chatroom == null) {
            return;
        }

        this.userToChatrooms.get(user).remove(chatroom);
        this.chatroomToUsers.get(chatroom).remove(user);

        System.out.println(user.getName() + " left room " + chatroom.getTitle());
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
}
