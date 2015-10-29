package com.busgen.bustalk.server.group;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.user.IUser;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.List;

public class GroupHandler {
    private final BiMap<String, IChatGroup> idToChatGroups;

    public GroupHandler() {
        this.idToChatGroups = Maps.synchronizedBiMap(HashBiMap.<String, IChatGroup>create());
    }

    public void leaveGroup(IUser user) {
        IChatGroup chatGroup = this.idToChatGroups.get(getGroupIdByUser(user));
        if (chatGroup != null) {
            chatGroup.leaveGroup(user);

            if (chatGroup.isEmpty()) {
                idToChatGroups.remove(getGroupIdByUser(user));
            }
        }
    }

    public void joinGroup(IUser user, String groupId) {
        IChatGroup chatGroup = idToChatGroups.get(groupId);
        if (chatGroup == null) {
            chatGroup = new ChatGroup(groupId);
            idToChatGroups.put(groupId, chatGroup);
        }

        chatGroup.joinGroup(user);
    }

    public boolean userIsInChatroom(IUser user, int chatId) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        return chatGroup.userIsInChatroom(user, chatId);
    }

    public void createRoom(IUser user, String title) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        chatGroup.createRoom(user, title);
    }

    public void joinRoom(IUser user, int chatId) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        chatGroup.joinRoom(user, chatId);
    }

    public void leaveRoom(IUser user, int chatId) {
        IChatGroup chatGroup = idToChatGroups.get(getGroupIdByUser(user));
        chatGroup.leaveRoom(user, chatId);
    }

    public List<IChatroom> getGroupRooms(String groupId) {
        IChatGroup chatGroup = idToChatGroups.get(groupId);
        return chatGroup.getChatrooms();
    }

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

    private IChatGroup getGroupByUser(IUser user) {
        for (IChatGroup chatGroup : this.idToChatGroups.values()) {
            if (chatGroup.isUserInGroup(user)) {
                return chatGroup;
            }
        }
        return null;
    }

    public String getGroupIdByUser(IUser user) {
        IChatGroup group = getGroupByUser(user);
        if (group != null) {
            return group.getGroupId();
        }
        return null;
    }
}
