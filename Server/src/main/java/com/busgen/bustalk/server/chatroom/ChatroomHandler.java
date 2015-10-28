package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.BusTalkSender;
import com.busgen.bustalk.server.user.IUser;
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

    private final BiMap<Integer, IChatroom> idToChatroom;
    private final ChatroomFactory chatroomFactory;
    private final static Logger LOGGER = Logger.getLogger(ChatroomHandler.class.getName());
    private final Map<String, List<IChatroom>> groupToListOfChatrooms;



    public ChatroomHandler() {
        this.idToChatroom = Maps.synchronizedBiMap(HashBiMap.<Integer, IChatroom>create());
        this.chatroomFactory = ChatroomFactory.getFactory();
        this.groupToListOfChatrooms = Collections.synchronizedMap(new HashMap<String, List<IChatroom>>());

    }

    /**
     * Creates a chatroom without a group ID
     *
     * @param user The user that's creating the chatroom
     * @param name The name/title of the chatroom
     * @return The newly created chatroom
     */
    public IChatroom createChatroom(IUser user, String name) {
        IChatroom chatroom = chatroomFactory.createChatroom(name);
        idToChatroom.put(chatroom.getIdNbr(), chatroom);

        List<IChatroom> tempList = groupToListOfChatrooms.get(user.getGroupId());
        if (tempList == null) {
            tempList = new ArrayList<IChatroom>();
            tempList.add(chatroom);
            groupToListOfChatrooms.put(user.getGroupId(), tempList);
        } else {
            tempList.add(chatroom);
        }

        LOGGER.log(Level.INFO, String.format("[{0}] Created chat \"{1}\" with id {2}"),
                new Object[]{user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
        return chatroom;

    }

    /**
     * Checks if a user is in a chatroom
     *
     * @param user
     * @param chatroom
     * @return
     */
    public boolean isUserInRoom(IUser user, IChatroom chatroom) {
        return chatroom.isUserInRoom(user);
    }

    /**
     * Adds a user to a charoom
     *
     * @param user
     * @param chatroom
     */
    public void joinChatroom(IUser user, IChatroom chatroom) {
        chatroom.subscribeToRoom(user);
        LOGGER.log(Level.INFO, String.format("[{0}] Joined room {1} ({2})"),
                    new Object[]{user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
    }

    /**
     * Removes a user from a chatroom
     *
     * @param user
     * @param chatroom
     */
    public void leaveChatroom(IUser user, IChatroom chatroom) {
        chatroom.unsubscribeToRoom(user);
        LOGGER.log(Level.INFO, String.format("[{0}] Left room {1} ({2})"),
                new Object[]{user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
        if(chatroom.getChatroomUsers().isEmpty() && chatroom.getIdNbr() > Constants.NBR_OF_RESERVED_CHAT_IDS - 1){
            deleteChatroom(chatroom.getIdNbr(), user.getGroupId());

        }
    }


    private void deleteChatroom(int chatId, String groupId) {
        IChatroom chatroom = idToChatroom.get(chatId);
        idToChatroom.remove(chatId);
        groupToListOfChatrooms.get(groupId).remove(chatroom);
        LOGGER.log(Level.INFO, String.format("Chat room {0} ({1}) was removed."), new Object[]{chatroom.getTitle(), chatId});
    }

    /**
     * @return a list of all chatroom
     */
    public List<IChatroom> getListOfOpenChatrooms() {
        return new ArrayList<IChatroom>(idToChatroom.values());
    }

    /**
     *
     * @param chatroom The chatroom for which a user list is requested
     * @return A list of users in the room
     */
    public List<IUser> getListOfUsersInChatroom(IChatroom chatroom) {
        return chatroom.getChatroomUsers();
    }

    /**
     * @param chatId the ID number of the chatroom that's being asked for
     * @return the chatroom with a matching ID
     */
    public IChatroom getChatroom(int chatId) {
        return idToChatroom.get(chatId);
    }

    /**
     * @param groupId the group ID of the chatrooms being asked for
     * @return a list of chatrooms with given groupId
     */
    public List<IChatroom> getGroupOfChatrooms(String groupId){
        return groupToListOfChatrooms.get(groupId);
    }


    public void createMainChatroom(String groupId){
        try{
            IChatroom chatroom = chatroomFactory.createMainChatroom(groupId);
            ArrayList<IChatroom> chatroomList = new ArrayList<IChatroom>();
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
        List<IChatroom> chatroomsToRemove = new ArrayList<IChatroom>(groupToListOfChatrooms.get(groupId));
        for(IChatroom c : chatroomsToRemove){
            deleteChatroom(c.getIdNbr(), groupId);
        }
        groupToListOfChatrooms.remove(groupId);
        LOGGER.log(Level.INFO, String.format("Group {0} was deleted"), groupId);

    }
}
