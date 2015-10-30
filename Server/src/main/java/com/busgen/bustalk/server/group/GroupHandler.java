package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.user.IUser;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class holds and handles each chat group, and makes sure the correct chat group is called for example when a user
 * wants to join a room.
 */
public class GroupHandler {
    private final BiMap<String, IChatGroup> idToChatGroups;
    private static final Logger LOGGER = Logger.getLogger(ChatGroup.class.getName());

    public GroupHandler() {
        this.idToChatGroups = Maps.synchronizedBiMap(HashBiMap.<String, IChatGroup>create());
    }

    /**
     * Leaves the group which the user is in.
     * @param user The user which is to leave its group.
     */
    public void leaveGroup(IUser user) {
        String groupId = getGroupIdByUser(user);
        IChatGroup chatGroup = this.idToChatGroups.get(groupId);
        if (chatGroup != null) {
            chatGroup.leaveGroup(user);

            if (chatGroup.isEmpty()) {
                idToChatGroups.remove(groupId);
                LOGGER.log(Level.INFO, String.format("Group \"{0}\" was deleted."),
                        groupId);
            }
        }
    }

    /**
     * Joins a group with a specific user.
     * @param user The user which is to join a group.
     * @param groupId The id of the group the user is to join.
     */
    public void joinGroup(IUser user, String groupId) {
        IChatGroup chatGroup = idToChatGroups.get(groupId);
        if (chatGroup == null) {
            chatGroup = new ChatGroup(groupId);
            idToChatGroups.put(groupId, chatGroup);
            LOGGER.log(Level.INFO, String.format("Group \"{0}\" was created."),
                    groupId);
        }

        chatGroup.joinGroup(user);
    }

    /**
     * Checks if a user is in a specific chat room.
     * @param user The user which is to be checked.
     * @param chatId The id of the chat to check.
     * @return True if the user is in the specified group, false otherwise.
     */
    public boolean userIsInChatroom(IUser user, int chatId) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        return chatGroup.userIsInChatroom(user, chatId);
    }

    /**
     * Creates a room with a title.
     * @param user The user creating the room.
     * @param title The title of the room.
     */
    public void createRoom(IUser user, String title) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        chatGroup.createRoom(user, title);
    }

    /**
     * Joins a room with a specific user.
     * @param user The user which is to join a room.
     * @param chatId The id of the chat which the user is to join.
     */
    public void joinRoom(IUser user, int chatId) {
        IChatGroup chatGroup = getGroupByUser(user);
        chatGroup.joinRoom(user, chatId);
    }

    /**
     * Leaves a room with a specific user.
     * @param user The user which is to leave a room.
     * @param chatId The id of the chat which the user is to leave.
     */
    public void leaveRoom(IUser user, int chatId) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        chatGroup.leaveRoom(user, chatId);
    }

    /**
     * @param groupId The id of a group
     * @return A list of chat rooms in the specified group
     */
    public List<IChatroom> getGroupRooms(String groupId) {
        IChatGroup chatGroup = idToChatGroups.get(groupId);
        return chatGroup.getChatrooms();
    }

    /**
     * @param chatId The id of a chat.
     * @return A list of users in the specified chat.
     */
    public List<IUser> getUsersInRoom(int chatId) {
        for (IChatGroup group : idToChatGroups.values()) {
            for (IChatroom chatroom : group.getChatrooms()) {
                if (chatroom.getIdNbr() == chatId) {
                    return group.getUsersInRoom(chatId);
                }
            }
        }
        return null;
    }

    /**
     * @param user A user.
     * @return The group the specified user is in.
     */
    private IChatGroup getGroupByUser(IUser user) {
        for (IChatGroup chatGroup : this.idToChatGroups.values()) {
            if (chatGroup.isUserInGroup(user)) {
                return chatGroup;
            }
        }
        return null;
    }

    /**
     * @param user A user.
     * @return The id of the group the specified user is in.
     */
    public String getGroupIdByUser(IUser user) {
        IChatGroup group = getGroupByUser(user);
        if (group != null) {
            return group.getGroupId();
        }
        return null;
    }

    /**
     * @param groupId An id of a group.
     * @return The group that has the specified group id.
     */
    public IChatGroup getGroupById(String groupId) {
        return this.idToChatGroups.get(groupId);
    }

}
