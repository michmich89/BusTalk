package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles incoming messages from client based on the type of message that was received.
 *
 * A singleton, due to the fact that every session created will have their own instance of <b>BusTalkServerEndpoint</b>.
 * As a singleton, all instances of BusTalkServerEndpoint talk to the same object of BusTalkHandler.
 *
 * Created by Kristoffer on 2015-10-08.
 */
public class BusTalkHandler {
    private final UserHandler userHandler;
    private final ChatroomHandler chatroomHandler;
    private final BusTalkSender messageSender;

    private static final Logger logger = Logger.getLogger(BusTalkHandler.class.getName());

    private static class Holder {
        static final BusTalkHandler INSTANCE = new BusTalkHandler();
    }

    public static BusTalkHandler getInstance(){
        return Holder.INSTANCE;
    }

    private BusTalkHandler(){

        this.userHandler = UserHandler.getInstance();
        this.chatroomHandler = ChatroomHandler.getInstance();
        this.messageSender = new BusTalkSender();


        this.chatroomHandler.createChatroom("test1", 0, "1");
        this.chatroomHandler.createChatroom("test2", 1, "2");
        this.chatroomHandler.createChatroom("test3", 2, "3");
        this.chatroomHandler.createChatroom("test4", 3, "4");
        this.chatroomHandler.createChatroom("test5", 4, "5");

    }

    /**
     * Method which will sort out what user sent what
     *
     * @param userMessage Message that a user sent
     * @param session Session from which the UserMessage came from
     */
    public void handleInput(UserMessage userMessage, Session session){
        logger.log(Level.INFO, String.format("Message received from client: {0}"),
                new Object[]{userMessage.toString()});
        try {
            int type = userMessage.getInt("type");
            User user = userHandler.getUser(session);
            if(user == null && type != MessageType.CHOOSE_NICKNAME_REQUEST){
                //TODO: Throw checked exception
                //throw new UserDoesNotExistException();
                throw new NullPointerException();
            } else if (user != null && user.getGroupId() == null && type != MessageType.CHANGE_GROUP_ID && type != MessageType.CHOOSE_NICKNAME_REQUEST) {
                //TODO: Throw checked exception
                throw new NullPointerException();
            }

                switch(type){
                case MessageType.CHAT_MESSAGE:
                    logger.log(Level.INFO, String.format("[{0}:{1}] Got message: {2}"),
                            new Object[]{session.getId(), user.getName(), userMessage.toString()});
                    sendChatMessage(userMessage, session);
                    break;

                case MessageType.CREATE_ROOM_REQUEST:
                {
                    String nameOfRoom = userMessage.getString("chatName");
                    Chatroom chatroom = chatroomHandler.createChatroom(user, nameOfRoom);
                    joinRoom(user, chatroom);
                    messageSender.chatroomCreatedNotification(user, chatroom);
                }
                break;
                case MessageType.JOIN_ROOM_REQUEST: {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);

                    if(canJoinRoom(user, chatroom)){
                        joinRoom(user, chatroom);
                        messageSender.userJoinedNotification(user, chatroom);
                    }
                }
                break;
                case MessageType.LIST_OF_USERS_IN_ROOM_REQUEST: {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);
                    messageSender.listOfUsersInRoom(user, chatroom);
                }
                    break;
                case MessageType.LIST_OF_ALL_CHATROOMS_REQUEST:
                    messageSender.listOfChatrooms(user);
                    break;
                case MessageType.LEAVE_ROOM_REQUEST: // Leave room
                {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);
                    leaveRoom(user, chatroom);
                }

                break;

                case MessageType.CHOOSE_NICKNAME_REQUEST: {
                    String name = userMessage.getString("name");
                    String interests = userMessage.getString("interests");
                    boolean status = userHandler.setUserNameAndInterests(user, session, name, interests);
                    messageSender.userNameAndInterestStatus(session, status);
                }
                    break;

                case MessageType.CHANGE_GROUP_ID: {
                    String id = userMessage.getString("groupId");
                    if(user.getGroupId() == null || !user.getGroupId().equalsIgnoreCase(id)) {
                        user.setGroupId(id);
                        logger.log(Level.INFO, String.format("[{0}:{1}] Group ID changed to {2}"),
                                new Object[]{session.getId(), user.getName(), user.getGroupId()});

                        for (Chatroom c : user.getCurrentChatrooms()) {
                            leaveRoom(user, c);
                        }
                    }
                    break;
                }

                default:

            }
        }catch(IllegalArgumentException e){
            //TODO: Vi ska skicka tillbaka information om Vad som gick fel
            e.printStackTrace();
        }catch(NullPointerException e){
            //TODO: What should we do with it? Send info saying that they need to create another user?
            //Create a user with a temporary name that they can change?
            e.printStackTrace();
        }
    }

    private void sendChatMessage(UserMessage userMessage, Session session) {
        int chatId = userMessage.getInt("chatId");
        String message = userMessage.getString("message");
        User sender = userHandler.getUser(session);
        Chatroom chatroom = chatroomHandler.getChatroom(chatId);

        if (chatroom.getChatroomUsers().contains(sender)) {
            messageSender.chatMessage(sender, chatroom, message);
        } else {
            logger.log(Level.INFO, String.format("[{0}:{1}] Could not send message to chat. User is not in chat room!"),
                    new Object[]{session.getId(), sender.getName()});
        }
    }

    /**
     * Removes the user (tied to the session) from all rooms it's connected to
     *
     * @param session The session that's tied to wanted user
     */
    public void removeSession(Session session){
        User user = userHandler.getUser(session);

        for(Chatroom c : user.getCurrentChatrooms()){
            Chatroom chatroom = c;
            leaveRoom(user, c);
        }
        userHandler.removeUser(user);
    }

    private boolean canJoinRoom(User user, Chatroom chatroom){

        boolean isInCorrectGroup = chatroomHandler.getGroupOfChatrooms(user.getGroupId()).contains(chatroom);

        return isInCorrectGroup && !chatroomHandler.isUserInRoom(user, chatroom) && !userHandler.isUserInRoom(user, chatroom);
    }

    private void joinRoom(User user, Chatroom chatroom){
        chatroomHandler.joinChatroom(user, chatroom);
        userHandler.addToCurrentRooms(user, chatroom);
    }

    private boolean canLeaveRoom(User user, Chatroom chatroom) {
        return chatroomHandler.isUserInRoom(user, chatroom) || userHandler.isUserInRoom(user, chatroom);
    }

    private void leaveRoom(User user, Chatroom chatroom) {
        if(canLeaveRoom(user, chatroom)) {
            chatroomHandler.leaveChatroom(user, chatroom);
            userHandler.removeFromCurrentRooms(user, chatroom);
            String groupId = user.getGroupId();

            if (chatroomHandler.getChatroom(chatroom.getIdNbr()) == null) {
                messageSender.chatDeletedNotification(groupId, chatroom);
            } else {
                messageSender.userLeftNotification(user, chatroom);
            }
        }
    }
}
