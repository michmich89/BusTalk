package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.BusTalkSender;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import com.busgen.bustalk.server.util.Constants;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatroomHandler {

    private final BiMap<Integer, Chatroom> idToChatroom;
    private final ChatroomFactory chatroomFactory;
    private final UserHandler userHandler;
    private final static Logger LOGGER = Logger.getLogger(ChatroomHandler.class.getName());
    private final Map<String, List<Chatroom>> groupToListOfChatrooms;

    private static class Holder {
        static final ChatroomHandler INSTANCE = new ChatroomHandler();
    }

    private ChatroomHandler() {
        this.idToChatroom = Maps.synchronizedBiMap(HashBiMap.<Integer, Chatroom>create());
        this.chatroomFactory = ChatroomFactory.getFactory();
        this.userHandler = UserHandler.getInstance();
        this.groupToListOfChatrooms = Collections.synchronizedMap(new HashMap<String, List<Chatroom>>());

    }

    public static ChatroomHandler getInstance() {
        return Holder.INSTANCE;
    }

    public Chatroom createChatroom(User user, String name) {
        Chatroom chatroom = chatroomFactory.createChatroom(name);
        idToChatroom.put(chatroom.getIdNbr(), chatroom);
        List<Chatroom> tempList = groupToListOfChatrooms.get(user.getGroupId());
        tempList.add(chatroom);

        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Created chat \"{2}\" with id {3}"),
                new Object[]{userHandler.getSession(user).getId(), user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
        joinChatroom(user, chatroom);
        return chatroom;

    }

    public void createChatroom(String name, int chatId, String groupId) {
        Chatroom chatroom = chatroomFactory.createChatroom(name, chatId);


        /*
        TODO:
        Chatrum ska alltid ha ett gruppID(?)
        Chatrum ska delas upp i listor baserat på detta ID
        Chat
         */

        if (chatroom != null) {
            idToChatroom.put(chatroom.getIdNbr(), chatroom);
            List<Chatroom> listChatroom= groupToListOfChatrooms.get(groupId);
            if(listChatroom == null){
                ArrayList<Chatroom> tempList = new ArrayList<Chatroom>();
                tempList.add(chatroom);
                groupToListOfChatrooms.put(groupId, tempList);
            }else{
                listChatroom.add(chatroom);
            }
        }
    }

    public boolean isUserInRoom(User user, Chatroom chatroom) {
        return chatroom.isUserInRoom(user);
    }

    public void joinChatroom(User user, Chatroom chatroom) {
        chatroom.subscribeToRoom(user);
        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Joined room {2} ({3})"),
                    new Object[]{userHandler.getSession(user).getId(), user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
    }

    public void leaveChatroom(User user, Chatroom chatroom) {
        chatroom.unsubscribeToRoom(user);
        LOGGER.log(Level.INFO, String.format("[{0}] Left room {1} ({2})"),
                new Object[]{userHandler.getSession(user).getId(), chatroom.getTitle(), chatroom.getIdNbr()});
        if(chatroom.getChatroomUsers().isEmpty() && chatroom.getIdNbr() > Constants.NBR_OF_RESERVED_CHAT_IDS - 1){
            deleteChatroom(chatroom.getIdNbr(), user.getGroupId());
        }
    }

    private void deleteChatroom(int chatId, String groupId) {
        Chatroom chatroom = idToChatroom.get(chatId);
        idToChatroom.remove(chatId);
        groupToListOfChatrooms.get(groupId).remove(chatroom);

        LOGGER.log(Level.INFO, String.format("Chat room {0} ({1}) was removed."), new Object[]{chatroom.getTitle(), chatId});
    }

    public List<Chatroom> getListOfOpenChatrooms() {
        return new ArrayList<Chatroom>(idToChatroom.values());
    }

    public List<User> getListOfUsersInChatroom(int chatId) {
        Chatroom chatroom = idToChatroom.get(chatId);
        return chatroom.getChatroomUsers();
    }

    public Chatroom getChatroom(int chatId) {
        return idToChatroom.get(chatId);
    }

    public List<Chatroom> getGroupOfChatrooms(String groupId){
        return groupToListOfChatrooms.get(groupId);
    }

    // TODO: Create method that checks if there's a user for the session
}
