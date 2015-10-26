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

/**
 * Handles all chat room related things, such as the id's of the chat rooms, the groups that the chat rooms are in, etc.
 */
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
        return chatroom;

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
     * @param chatroom The chatroom for which a user list is requested
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


    public void createMainChatroom(String groupId){
        try{
            Chatroom chatroom = chatroomFactory.createMainChatroom(groupId);
            ArrayList<Chatroom> chatroomList = new ArrayList<Chatroom>();
            chatroomList.add(chatroom);
            idToChatroom.put(chatroom.getIdNbr(), chatroom);
            groupToListOfChatrooms.put(groupId, chatroomList);

            LOGGER.log(Level.INFO, String.format("Main chatroom with title {0} and group ID {1} was created with ID-number {2}"),
                    new Object[]{chatroom.getTitle(), groupId, chatroom.getIdNbr()});
        }catch(ChatIdNbrFullException e){
            e.printStackTrace();
            /*
            TODO: Some actual exception handling...
             */
        }
    }

    public void removeGroup(String groupId){
        List<Chatroom> chatroomsToRemove = new ArrayList<Chatroom>(groupToListOfChatrooms.get(groupId));
        for(Chatroom c : chatroomsToRemove){
            deleteChatroom(c.getIdNbr(), groupId);
        }
        groupToListOfChatrooms.remove(groupId);
        LOGGER.log(Level.INFO, String.format("Group {0} was deleted"), groupId);

    }
}
