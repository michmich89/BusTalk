package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.IUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgUsersInChat implements IServerMessage{
    private List<IUser> userList;
    private int chatId;

    public MsgUsersInChat(int chatId){
        this.chatId = chatId;
        userList = new ArrayList<IUser>();
    }

    public List<IUser> getUserList(){
        return userList;
    }

    public void addUserToList(IUser user){
        userList.add(user);
    }

    public void addUsersToList(List<IUser> users) {
        userList.addAll(users);
    }

    public int getChatID(){
        return chatId;
    }
}
