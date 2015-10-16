package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IUser;
import com.busgen.bustalk.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgUsersInChat {
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

    public int getChatID(){
        return chatId;
    }
}
