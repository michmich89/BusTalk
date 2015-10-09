package com.busgen.bustalk.model;

import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Johan on 2015-10-05.
 */
public class Client implements IClient{

    private IUser user;
    private List<IChatroom> chatrooms;

    //Singleton pattern
    private static Client client = null;

    private Client (IUser user){
        this.user = user;
        chatrooms = new ArrayList<IChatroom>();
    }

    public static Client getInstance(){
        if(client == null){
            client = new Client(new User());
        }return client;
    }


    @Override
    public String getUserName() {
        return user.getUserName();
    }

    @Override
    public String getInterest() {
        return user.getInterest();
    }

    @Override
    public void setUser(IUser user) {
        this.user = user;
    }

    @Override
    public void setUserName(String userName) {
        user.setUserName(userName);
    }

    @Override
    public void setInterest(String interest) {
        user.setInterest(interest);
    }

    @Override
    public IUser getUser() {
        return user;
    }

    @Override
    public Collection<IChatroom> getChatrooms() {
        return chatrooms;
    }

    @Override
    public Collection<IServerMessage> recieveMessages() {
        return null;
    }

    @Override
    public void sendMessage(IServerMessage serverMessage) {

    }

    @Override
    public void joinRoom(IChatroom chatroom) {
        chatrooms.add(chatroom);
    }

    @Override
    public void leaveRoom(IChatroom chatroom) {
        if (chatrooms.contains(chatroom)){
            chatrooms.remove(chatroom);
        }
    }

	@Override
	public void addMessageToChatroom(MsgChatMessage message) {
		for(int i = 0; i<chatrooms.size(); i++){
			if(chatrooms.get(i).getChatID()==message.getChatId()){
				chatrooms.get(i).addMessage(message);
			}
		}
	}
}
