package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.IChatroom;
import com.busgen.bustalk.server.group.GroupHandler;
import com.busgen.bustalk.server.message.BusTalkSender;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.IUser;
import com.busgen.bustalk.server.user.UserHandler;

import javax.websocket.Session;
import java.util.List;
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
    private final GroupHandler groupHandler;
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
        this.groupHandler = new GroupHandler();
        this.messageSender = BusTalkSender.getInstance();
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
                    sendChatMessageRequest(userMessage, user, session);
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
        } else if (user != null && groupHandler.getGroupIdByUser(user) == null && type != MessageType.CHANGE_GROUP_ID &&
                type != MessageType.CHOOSE_NICKNAME_REQUEST) {
            //TODO: Throw checked exception
            throw new NullPointerException();
        }
    }

    private void sendChatMessageRequest(UserMessage userMessage, IUser user, Session session) {
        int chatId = userMessage.getInt("chatId");
        String message = userMessage.getString("message");

        if (groupHandler.userIsInChatroom(user, chatId)) {
            List<IUser> usersInChatroom = groupHandler.getUsersInRoom(chatId);
            messageSender.chatMessage(user, usersInChatroom, chatId, message);
        } else {
            logger.log(Level.INFO, String.format("[{0}:{1}] Could not send message to chat. User is not in chat room!"),
                    new Object[]{session.getId(), user.getName()});
        }
    }

    private void createRoomRequest(UserMessage userMessage, IUser user) {
        String nameOfRoom = userMessage.getString("chatName");
        groupHandler.createRoom(user, nameOfRoom);
    }

    private void joinRoomRequest(UserMessage userMessage, IUser user) {
        int chatId = userMessage.getInt("chatId");
        groupHandler.joinRoom(user, chatId);
    }

    private void getListOfUsersInRoomRequest(UserMessage userMessage, IUser user) {
        int chatId = userMessage.getInt("chatId");
        List<IUser> listOfUsers = groupHandler.getUsersInRoom(chatId);
        messageSender.listOfUsersInRoom(user, listOfUsers, chatId);
    }

    private void getListOfAllChatroomsRequest(IUser user) {
        String groupId = groupHandler.getGroupIdByUser(user);
        List<IChatroom> chatrooms = groupHandler.getGroupRooms(groupId);
        messageSender.listOfChatrooms(user, chatrooms, groupId);
    }

    private void leaveRoomRequest(UserMessage userMessage, IUser user) {
        int chatId = userMessage.getInt("chatId");
        groupHandler.leaveRoom(user, chatId);
    }

    private void setUserNameRequest(UserMessage userMessage, IUser user, Session session) {
        String name = userMessage.getString("name");
        String interests = userMessage.getString("interests");
        boolean status = userHandler.setUserNameAndInterests(user, session, name, interests);
        messageSender.userNameAndInterestStatus(session, status);
    }

    private void setGroupRequest(UserMessage userMessage, IUser user, Session session) {
        String oldIdNbr = groupHandler.getGroupIdByUser(user);
        String id = userMessage.getString("groupId");
        if(oldIdNbr == null || !oldIdNbr.equals(id)) {
            groupHandler.leaveGroup(user);
            groupHandler.joinGroup(user, id);
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
        groupHandler.leaveGroup(user);
        userHandler.removeUser(user);
    }

    //GETTERS FOR TESTING PURPOSE
    public UserHandler getUserHandler(){
        return this.userHandler;
    }

    public GroupHandler getGroupHandler() {
        return this.groupHandler;
    }
}
