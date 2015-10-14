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

    /**
     * Creates a chatroom without a group ID
     *
     * @param user The user that's creating the chatroom
     * @param name The name/title of the chatroom
     * @return The newly created chatroom
     */
    public Chatroom createChatroom(User user, String name) {
        Chatroom chatroom = chatroomFactory.createChatroom(name);
        idToChatroom.put(chatroom.getIdNbr(), chatroom);

        List<Chatroom> tempList = groupToListOfChatrooms.get(user.getGroupId());
        if (tempList == null) {
            tempList = new ArrayList<Chatroom>();
            tempList.add(chatroom);
            groupToListOfChatrooms.put(user.getGroupId(), tempList);
        } else {
            tempList.add(chatroom);
        }

        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Created chat \"{2}\" with id {3}"),
                new Object[]{userHandler.getSession(user).getId(), user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
        joinChatroom(user, chatroom);
        return chatroom;

    }

    /**
     * Creates a chatroom with a group ID (Used for testing purpose?)
     *
     * @param name The name/title of the chatroom
     * @param chatId The specific ID of the chatroom
     * @param groupId The specific group ID
     */
    public void createChatroom(String name, int chatId, String groupId) {
        Chatroom chatroom = chatroomFactory.createChatroom(name, chatId);
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

    /**
     * Checks if a user is in a chatroom
     *
     * @param user
     * @param chatroom
     * @return
     */
    public boolean isUserInRoom(User user, Chatroom chatroom) {
        return chatroom.isUserInRoom(user);
    }

    /**
     * Adds a user to a charoom
     *
     * @param user
     * @param chatroom
     */
    public void joinChatroom(User user, Chatroom chatroom) {
        chatroom.subscribeToRoom(user);
        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Joined room {2} ({3})"),
                    new Object[]{userHandler.getSession(user).getId(), user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
    }

    /**
     * Removes a user from a chatroom
     *
     * @param user
     * @param chatroom
     */
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

    /**
     * @return a list of all chatroom
     */
    public List<Chatroom> getListOfOpenChatrooms() {
        return new ArrayList<Chatroom>(idToChatroom.values());
    }

    /**
     *
     * @param chatId The ID number of the chatroom a user list is requested
     * @return A list of users in the room
     */
    public List<User> getListOfUsersInChatroom(Chatroom chatroom) {
        return chatroom.getChatroomUsers();
    }

    /**
     * @param chatId the ID number of the chatroom that's being asked for
     * @return the chatroom with a matching ID
     */
    public Chatroom getChatroom(int chatId) {
        return idToChatroom.get(chatId);
    }

    /**
     * @param groupId the group ID of the chatrooms being asked for
     * @return a list of chatrooms with given groupId
     */
    public List<Chatroom> getGroupOfChatrooms(String groupId){
        return groupToListOfChatrooms.get(groupId);
    }

}
