package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageSender;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by Kristoffer on 2015-10-08.
 */
public class BusTalkHandler {
    private final UserHandler userHandler;
    private final ChatroomHandler chatroomHandler;
    private final MessageSender messageSender;

    private static final Logger logger = Logger.getLogger(BusTalkHandler.class.getName());

    private static class Holder {
        static final BusTalkHandler INSTANCE = new BusTalkHandler();
    }

    public synchronized static BusTalkHandler getInstance(){
        return Holder.INSTANCE;
    }

    private BusTalkHandler(){

        this.userHandler = UserHandler.getInstance();
        this.chatroomHandler = ChatroomHandler.getInstance();
        this.messageSender = MessageSender.getInstance();

        this.chatroomHandler.createChatroom("test1", 0);
        this.chatroomHandler.createChatroom("test2", 1);
        this.chatroomHandler.createChatroom("test3", 2);
        this.chatroomHandler.createChatroom("test4", 3);
        this.chatroomHandler.createChatroom("test5", 4);

    }

    public void handleInput(UserMessage userMessage, Session session){
        try {
            int type = userMessage.getInt("type");

            switch(type){
                case MessageType.CHAT_MESSAGE:
                    sendChatMessage(userMessage, session);
                    break;

                case MessageType.CREATE_ROOM_REQUEST:
                {
                    String nameOfRoom = userMessage.getString("chatName");
                    User user = userHandler.getUser(session);
                    chatroomHandler.createChatroom(user, nameOfRoom);
                }
                break;
                case MessageType.JOIN_ROOM_REQUEST:
                {
                    int chatId = userMessage.getInt("chatId");
                    User user = userHandler.getUser(session);
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);
                    chatroomHandler.joinChatroom(user, chatroom);
                }
                break;
                case MessageType.LIST_OF_USERS_IN_ROOM_REQUEST: {
                    int chatId = userMessage.getInt("chatId");
                    User user = userHandler.getUser(session);
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);
                    messageSender.listOfUsersInRoom(user, chatroom);
                }
                    break;
                case MessageType.LIST_OF_ALL_CHATROOMS_REQUEST:
                    messageSender.listOfChatrooms(userHandler.getUser(session));
                    break;
                case MessageType.LEAVE_ROOM_REQUEST: // Leave room
                {
                    int chatId = userMessage.getInt("chatId");
                    User user = userHandler.getUser(session);
                    chatroomHandler.leaveChatroom(user, chatId);
                }

                break;

                case MessageType.CHOOSE_NICKNAME_REQUEST: {
                    String name = userMessage.getString("name");
                    String interests = userMessage.getString("interests");
                    User user = userHandler.getUser(session);
                    userHandler.setUserNameAndInterests(user, session, name, interests);
                }
                    break;
                case MessageType.NICKNAME_AVAILABLE_CHECK:
                    break;

                default:

            }
        }catch(IllegalArgumentException e){
            //TODO: Vi ska skicka tillbaka information om Vad som gick fel
            session.getAsyncRemote().sendText(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendChatMessage(UserMessage userMessage, Session session) {
        User sender = userHandler.getUser(session);

        int chatId = userMessage.getInt("chatId");
        String message = userMessage.getString("message");

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.CHAT_MESSAGE_NOTIFICATION);
        jsonObject.put("chatId", chatId);
        jsonObject.put("sender", sender.getName());
        jsonObject.put("message", message);
        jsonObject.put("time", new Date().toString());
        for (User u : chatroomHandler.getListOfUsersInChatroom(chatId)) {
            Session s = userHandler.getSession(u);
            s.getAsyncRemote().sendObject(jsonObject);
        }
    }

    private void setNameAndInterest(UserMessage message, Session session) {
        String name = message.getString("name");
        String interests = message.getString("interests");
        User user = userHandler.getUser(session);

        boolean status = userHandler.setUserNameAndInterests(user, session, name, interests);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.CREDENTIAL_CHANGE_NOTIFICATION);
        jsonObject.put("status", status);
        session.getAsyncRemote().sendObject(jsonObject);
    }

}
