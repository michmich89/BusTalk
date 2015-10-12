package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.Chatroom;
import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserDoesNotExistException;
import com.busgen.bustalk.server.user.UserHandler;

import javax.websocket.Session;
import java.util.Iterator;
import java.util.logging.Logger;

/**
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


        this.chatroomHandler.createChatroom("test1", 0);
        this.chatroomHandler.createChatroom("test2", 1);
        this.chatroomHandler.createChatroom("test3", 2);
        this.chatroomHandler.createChatroom("test4", 3);
        this.chatroomHandler.createChatroom("test5", 4);

    }

    /**
     * Method which will sort out what user sent what
     *
     * @param userMessage Message that a user sent
     * @param session Session from which the UserMessage came from
     */
    public void handleInput(UserMessage userMessage, Session session){
        try {
            int type = userMessage.getInt("type");
            User userCheck = userHandler.getUser(session);
            // TODO: Check if user exists or if type is CHOOSE_NICKNAME_REQUEST, throw exception if none is true
            if(userCheck == null){
                //TODO: This only sends the code to the catch, but maybe thats enough for now?
                //TODO: Do we even want to throw an exception?
                //throw new UserDoesNotExistException();
                throw new NullPointerException();
            }
            switch(type){
                case MessageType.CHAT_MESSAGE:
                    sendChatMessage(userMessage, session);
                    break;

                case MessageType.CREATE_ROOM_REQUEST:
                {
                    String nameOfRoom = userMessage.getString("chatName");
                    User user = userHandler.getUser(session);
                    Chatroom chatroom = chatroomHandler.createChatroom(user, nameOfRoom);
                    messageSender.chatroomCreatedNotification(user, chatroom);
                }
                break;
                case MessageType.JOIN_ROOM_REQUEST: {
                    int chatId = userMessage.getInt("chatId");
                    System.out.println("Före");
                    User user = userHandler.getUser(session);
                    System.out.println("Efter");
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);

                    if(canJoinRoom(user, chatroom)){
                        joinRoom(user, chatroom);
                        messageSender.userJoinedNotification(user, chatroom);
                    }
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
                    Chatroom chatroom = chatroomHandler.getChatroom(chatId);
                    chatroomHandler.leaveChatroom(user, chatroom);
                    userHandler.removeFromCurrentRooms(user, chatroom);

                    if(chatroomHandler.getChatroom(chatId) == null){
                        messageSender.chatDeletedNotification(chatroom);
                    }else{
                        messageSender.userLeftNotification(user, chatroom);
                    }
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
                    //TODO: Implement?

                    break;

                default:

            }
        }catch(IllegalArgumentException e){
            //TODO: Vi ska skicka tillbaka information om Vad som gick fel
            session.getAsyncRemote().sendText(e.getMessage());
            e.printStackTrace();
        }catch(NullPointerException e){
            //TODO: What should we do with it? Send info saying that they need to create another user?
            //Create a user with a temporary name that they can change?
        }
    }

    private void sendChatMessage(UserMessage userMessage, Session session) {
        int chatId = userMessage.getInt("chatId");
        String message = userMessage.getString("message");
        User sender = userHandler.getUser(session);
        Chatroom chatroom = chatroomHandler.getChatroom(chatId);
        messageSender.chatMessage(sender, chatroom, message);
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

            chatroomHandler.leaveChatroom(user, c);

            if(chatroomHandler.getChatroom(chatroom.getIdNbr()) == null){
                messageSender.chatDeletedNotification(chatroom);
            }else{
                messageSender.userLeftNotification(user, chatroom);
            }
        }

/*        Iterator<Chatroom> iterator = chatroomHandler.getListOfOpenChatrooms().iterator();
        while (iterator.hasNext()){
            Chatroom chatroom = iterator.next();

            chatroomHandler.leaveChatroom(user, chatroom);

            if(chatroomHandler.getChatroom(chatroom.getIdNbr()) == null){
                messageSender.chatDeletedNotification(chatroom);
            }else{
                messageSender.userLeftNotification(user, chatroom);
            }
        }
*/
        userHandler.removeUser(user);
    }

    private boolean canJoinRoom(User user, Chatroom chatroom){
        return (chatroomHandler.canJoinRoom(user, chatroom) && userHandler.canJoinRoom(user, chatroom));
    }

    private void joinRoom(User user, Chatroom chatroom){
        chatroomHandler.joinChatroom(user, chatroom);
        userHandler.addToCurrentRooms(user, chatroom);
    }

}
