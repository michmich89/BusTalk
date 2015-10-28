package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.IUser;
import com.busgen.bustalk.server.user.UserHandler;

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
        this.userHandler = new UserHandler();
        this.chatroomHandler = new ChatroomHandler();
        this.messageSender = new BusTalkSender(userHandler ,chatroomHandler);
    }

    /**
     * Reads the type of the UserMessage and does appropriate work based on the type
     *
     * @param userMessage Message that a user sent
     * @param session Session from which the UserMessage came from
     */
    public void handleInput(UserMessage userMessage, Session session){
        logger.log(Level.INFO, String.format("Message received from client: {0}"),
                new Object[]{userMessage.toString()});
        try {
            int type = userMessage.getInt("type");
            IUser user = userHandler.getUser(session);

            checkUserPermissions(user, type);

                switch(type){
                case MessageType.CHAT_MESSAGE:
                    sendChatMessageRequest(userMessage, session);
                    break;

                case MessageType.CREATE_ROOM_REQUEST:
                    createRoomRequest(userMessage, user);
                    break;

                case MessageType.JOIN_ROOM_REQUEST:
                    joinRoomRequest(userMessage, user);
                    break;

                case MessageType.LIST_OF_USERS_IN_ROOM_REQUEST:
                    getListOfUsersInRoomRequest(userMessage, user);
                    break;

                case MessageType.LIST_OF_ALL_CHATROOMS_REQUEST:
                    getListOfAllChatroomsRequest(user);
                    break;

                case MessageType.LEAVE_ROOM_REQUEST:
                    leaveRoomRequest(userMessage, user);
                    break;

                case MessageType.CHOOSE_NICKNAME_REQUEST:
                    setUserNameRequest(userMessage, user, session);
                    break;

                case MessageType.CHANGE_GROUP_ID: {
                    setGroupRequest(userMessage, user, session);
                    break;
                }

                default:

            }
        }catch(IllegalArgumentException e){
            //TODO: Inform the user what went wrong
            e.printStackTrace();
        }catch(NullPointerException e){
            //TODO: What should we do with it? Send info saying that they need to create another user?
            //Create a user with a temporary name that they can change?
            e.printStackTrace();
        }
    }

    /* === Methods used to process messages sent from clients === */

    private void checkUserPermissions(IUser user, int type) {
        // If user does not exist a user must send a request to set nick and interests
        if(user == null && type != MessageType.CHOOSE_NICKNAME_REQUEST){
            //TODO: Throw checked exception
            //throw new UserDoesNotExistException();
            throw new NullPointerException();
            // If a user does exist, but not in a group, the user is only allowed to request name change or join a group
        } else if (user != null && user.getGroupId() == null && type != MessageType.CHANGE_GROUP_ID &&
                type != MessageType.CHOOSE_NICKNAME_REQUEST) {
            //TODO: Throw checked exception
            throw new NullPointerException();
        }
    }

    private void sendChatMessageRequest(UserMessage userMessage, Session session) {
        int chatId = userMessage.getInt("chatId");
        String message = userMessage.getString("message");
        IUser sender = userHandler.getUser(session);
        IChatroom chatroom = chatroomHandler.getChatroom(chatId);

        if (chatroom.getChatroomUsers().contains(sender)) {
            messageSender.chatMessage(sender, chatroom, message);
        } else {
            logger.log(Level.INFO, String.format("[{0}:{1}] Could not send message to chat. User is not in chat room!"),
                    new Object[]{session.getId(), sender.getName()});
        }
    }

    private void createRoomRequest(UserMessage userMessage, IUser user) {
        String nameOfRoom = userMessage.getString("chatName");
        IChatroom chatroom = chatroomHandler.createChatroom(user, nameOfRoom);
        joinRoom(user, chatroom);
        messageSender.chatroomCreatedNotification(user, chatroom);
    }

    private void joinRoomRequest(UserMessage userMessage, IUser user) {
        int chatId = userMessage.getInt("chatId");
        IChatroom chatroom = chatroomHandler.getChatroom(chatId);

        if(canJoinRoom(user, chatroom)){
            joinRoom(user, chatroom);
            messageSender.userJoinedNotification(user, chatroom);
        }
    }

    private void getListOfUsersInRoomRequest(UserMessage userMessage, IUser user) {
        int chatId = userMessage.getInt("chatId");
        IChatroom chatroom = chatroomHandler.getChatroom(chatId);
        messageSender.listOfUsersInRoom(user, chatroom);
    }

    private void getListOfAllChatroomsRequest(IUser user) {
        messageSender.listOfChatrooms(user);
    }

    private void leaveRoomRequest(UserMessage userMessage, IUser user) {
        int chatId = userMessage.getInt("chatId");
        IChatroom chatroom = chatroomHandler.getChatroom(chatId);
        leaveRoom(user, chatroom);
    }

    private void setUserNameRequest(UserMessage userMessage, IUser user, Session session) {
        String name = userMessage.getString("name");
        String interests = userMessage.getString("interests");
        boolean status = userHandler.setUserNameAndInterests(user, session, name, interests);
        messageSender.userNameAndInterestStatus(session, status);
    }

    private void setGroupRequest(UserMessage userMessage, IUser user, Session session) {
        String oldIdNbr = user.getGroupId();
        String id = userMessage.getString("groupId");
        if(user.getGroupId() == null || !user.getGroupId().equals(id)) {

            logger.log(Level.INFO, String.format("[{0}:{1}] Group ID changed to {2}"),
                    new Object[]{session.getId(), user.getName(), user.getGroupId()});

            for (IChatroom c : user.getCurrentChatrooms()) {
                leaveRoom(user, c);
            }
            user.setGroupId(id);
            if(chatroomHandler.getGroupOfChatrooms(id) == null ||
                    chatroomHandler.getGroupOfChatrooms(id).isEmpty()) {

                chatroomHandler.createMainChatroom(id);

            }
            if(oldIdNbr != null) {
                removeGroupIfNeeded(oldIdNbr);
            }
        }
    }

    /* End of methods used to process messages sent from clients */

    /**
     * Removes the user (tied to the session) from all rooms it's connected to
     *
     * @param session The session that's tied to wanted user
     */
    public void removeSession(Session session){
        IUser user = userHandler.getUser(session);

        for(IChatroom c : user.getCurrentChatrooms()){
            IChatroom chatroom = c;
            leaveRoom(user, c);
        }
        userHandler.removeUser(user);
        removeGroupIfNeeded(user.getGroupId());
    }

    private boolean canJoinRoom(IUser user, IChatroom chatroom){

        boolean isInCorrectGroup = chatroomHandler.getGroupOfChatrooms(user.getGroupId()).contains(chatroom);
        boolean isAlreadyInRoom = chatroomHandler.isUserInRoom(user, chatroom) || userHandler.isUserInRoom(user, chatroom);

        return isInCorrectGroup && !isAlreadyInRoom;
    }

    private void joinRoom(IUser user, IChatroom chatroom){
        chatroomHandler.joinChatroom(user, chatroom);
        userHandler.addToCurrentRooms(user, chatroom);
    }

    private boolean canLeaveRoom(IUser user, IChatroom chatroom) {
        return chatroomHandler.isUserInRoom(user, chatroom) || userHandler.isUserInRoom(user, chatroom);
    }

    private void leaveRoom(IUser user, IChatroom chatroom) {
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

    private void removeGroupIfNeeded(String groupId){
        for(IUser u : userHandler.getUsers()){
            if (u.getGroupId().equals(groupId)){
                return;
            }
        }
        chatroomHandler.removeGroup(groupId);
    }

    //GETTERS FOR TESTING PURPOSE
    public UserHandler getUserHandler(){
        return this.userHandler;
    }

    public ChatroomHandler getChatroomHandler(){
        return this.chatroomHandler;
    }

}
