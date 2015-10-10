import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.json.JSONObject;

import javax.websocket.Session;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kristoffer on 2015-10-08.
 */
public class BusTalkHandler {
    private final Map<Integer, Chatroom> idToChatroom;
    private final BiMap<User, Session> userToSession;
    private final List<String> disallowedNames;
    private final Logger LOGGER;
    private final ChatroomFactory chatroomFactory;



    private static class Holder {
        static final BusTalkHandler INSTANCE = new BusTalkHandler();
    }

    public synchronized static BusTalkHandler getInstance(){
        return Holder.INSTANCE;
    }

    private BusTalkHandler(){

        idToChatroom = Collections.synchronizedMap(new HashMap<Integer, Chatroom>());
        userToSession = Maps.synchronizedBiMap(HashBiMap.<User, Session>create());
        disallowedNames = Collections.synchronizedList(new ArrayList<String>());

        disallowedNames.add("Alexander Kloutschek"); //TIHI
        LOGGER = Logger.getLogger(BusTalkServerEndpoint.class.getName());
        chatroomFactory = ChatroomFactory.getFactory();

        //TEST ROOMS

        Chatroom testRoom1 = new Chatroom(1, "test1");
        Chatroom testRoom2 = new Chatroom(2, "test1");
        Chatroom testRoom3 = new Chatroom(3, "test1");
        Chatroom testRoom4 = new Chatroom(4, "test1");
        Chatroom testRoom5 = new Chatroom(5, "test1");

        idToChatroom.put(1, testRoom1);
        idToChatroom.put(2, testRoom2);
        idToChatroom.put(3, testRoom3);
        idToChatroom.put(4, testRoom4);
        idToChatroom.put(5, testRoom5);
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
                    Chatroom chatroom = chatroomFactory.createChatroom(nameOfRoom);
                    int chatId = chatroom.getIdNbr();

                    idToChatroom.put(chatId, chatroom);

                    LOGGER.log(Level.INFO, String.format("[{0}] Created chat \"{1}\" with id {2}\n Existing chat rooms: {3}"),
                            new Object[]{session.getId(), nameOfRoom, chatId, idToChatroom.values().toString()});

                    joinRoom(userToSession.inverse().get(session), chatroom);

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", MessageType.ROOM_CREATED_NOTIFICATION);
                    jsonObject.put("title", nameOfRoom);
                    jsonObject.put("chatId", chatId);
                    jsonObject.put("isYours", false);
                    for (Session s : userToSession.values()) {
                        if(!s.equals(session)) {
                            s.getAsyncRemote().sendObject(jsonObject);
                        }
                    }
                    jsonObject.put("isYours", true);
                    session.getAsyncRemote().sendObject(jsonObject);

                }
                break;
                case MessageType.JOIN_ROOM_REQUEST:
                {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatRoom = idToChatroom.get(chatId);

                    User user = userToSession.inverse().get(session);
                    joinRoom(user, chatRoom);

                }
                break;
                case MessageType.LIST_OF_USERS_IN_ROOM_REQUEST:
                    sendListOfUsersInChat(userMessage, session);
                    break;
                case MessageType.LIST_OF_ALL_CHATROOMS_REQUEST:
                    sendListOfChatrooms(userMessage, session);
                    break;
                case MessageType.LEAVE_ROOM_REQUEST: // Leave room
                {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatroom = idToChatroom.get(chatId);

                    User user = userToSession.inverse().get(session);
                    if(!chatroom.unsubscribeToRoom(user) || user == null){
                        break;
                    }

                    LOGGER.log(Level.INFO, String.format("[{0}] Left room {1} ({2})"), new Object[]{session.getId(), chatroom.getTitle(), chatId});
                    if(chatroom.getChatroomUsers().isEmpty() && chatroom.getIdNbr() > Constants.NBR_OF_RESERVED_CHAT_IDS - 1){
                        deleteRoom(chatroom);
                    } else {
                        userLeftRoomNotification(chatroom, userToSession.inverse().get(session));
                    }
                }

                break;

                case MessageType.CHOOSE_NICKNAME_REQUEST:
                    setNameAndInterest(userMessage, session);

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

    private void addDisallowedName(String name){
        name = name.toLowerCase();
        if(!disallowedNames.contains(name)) {
            disallowedNames.add(name);
            LOGGER.log(Level.INFO, String.format("Added \"{0}\" to list of disallowed names"), name);
        }
    }

    private void removeDisallowedName(String name){
        name = name.toLowerCase();
        disallowedNames.remove(name);
        LOGGER.log(Level.INFO, String.format("Removed \"{0}\" from list of disallowed names"), name);
    }

    private boolean isNameAllowed(String name) {
        return !disallowedNames.contains(name.toLowerCase());
    }

    private void addUser(User user, Session session){
        userToSession.put(user, session);
        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Added to user list"), new Object[]{session.getId(), user.getName()});
        addDisallowedName(user.getName());
        LOGGER.log(Level.INFO, String.format("\"{0}\" was added to the list of disallowed names"), user.getName());
    }

    private void removeUser(User user){
        removeDisallowedName(user.getName());
        LOGGER.log(Level.INFO, String.format("\"{0}\" was removed from the list of disallowed names"), user.getName());
        userToSession.remove(user);
        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Removed from user list"), new Object[]{userToSession.get(user).getId(), user.getName()});
    }

    private void sendChatMessage(UserMessage userMessage, Session session) {
        User sender = userToSession.inverse().get(session);

        int chatId = userMessage.getInt("chatId");
        Chatroom chatRoom = idToChatroom.get(chatId);
        String message = userMessage.getString("message");

        for (User u : chatRoom.getChatroomUsers()) {
            Session s = userToSession.get(u);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type", MessageType.CHAT_MESSAGE_NOTIFICATION);
            jsonObject.put("chatId", chatId);
            jsonObject.put("sender", sender.getName());
            jsonObject.put("message", message);
            jsonObject.put("time", new Date().toString());

            s.getAsyncRemote().sendObject(jsonObject);
        }
    }

    private void setNameAndInterest(UserMessage message, Session session) {
        boolean status = true;

        String name = message.getString("name");
        String interests = message.getString("interests");
        User user = userToSession.inverse().get(session);

        if (user != null && (isNameAllowed(name) || user.getName().equals(name))) {
            String oldName = user.getName();
            String oldInterests = user.getInterests();

            user.setName(name);
            user.setInterests(interests);

            addUser(user, session);

            LOGGER.log(Level.INFO, String.format("[{0}:{1}] Name changed to \"{2}\" and interest changed from \"{3}\" to \"{4}\""),
                    new Object[]{session.getId(), oldName, user.getName(), oldInterests, user.getInterests()});
        } else if (isNameAllowed(name)) {
            user = new User(name, interests);
            addUser(user, session);

            LOGGER.log(Level.INFO, String.format("[{0}:{1}] Created with interests \"{2}\""),
                    new Object[]{session.getId(), user.getName(), user.getInterests()});
        } else {
            status = false;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.CREDENTIAL_CHANGE_NOTIFICATION);
        jsonObject.put("status", status);
        session.getAsyncRemote().sendObject(jsonObject);
    }

    private void sendListOfUsersInChat(UserMessage userMessage, Session session) {
        int chatId = userMessage.getInt("chatId");
        Chatroom chatRoom = idToChatroom.get(chatId);

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("type", MessageType.LIST_OF_USERS_IN_CHAT_NOTIFICATION);
        for (User u : chatRoom.getChatroomUsers()) {
            Session s = userToSession.get(u);
            User user = userToSession.inverse().get(session);


            JSONObject jsonUser = new JSONObject();
            jsonUser.put("name", user.getName());
            jsonUser.put("interests", user.getInterests());

            jsonObject.append("users", jsonUser);
        }

        session.getAsyncRemote().sendObject(jsonObject);
    }

    private void sendListOfChatrooms(UserMessage userMessage, Session session) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.LIST_OF_CHATROOMS_NOTIFICATION);

        Iterator iterator = idToChatroom.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            Chatroom chatroom = (Chatroom)pair.getValue();
            JSONObject jsonChatroom = new JSONObject();
            jsonChatroom.put("chatId", chatroom.getIdNbr());
            jsonChatroom.put("name", chatroom.getTitle());
            jsonObject.append("chatrooms", jsonChatroom);
        }

        session.getAsyncRemote().sendObject(jsonObject);

        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Sent list of chatrooms"),
                new Object[]{session.getId(), userToSession.inverse().get(session).getName()});
    }

    private void newUserInChatNotification(Chatroom chatroom, User user) {
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("type", MessageType.NEW_USER_IN_CHAT_NOTIFICATION); //What notification should be sent back?
        objectToSend.put("chatId", chatroom.getIdNbr());
        objectToSend.put("name", user.getName());
        objectToSend.put("interests", user.getInterests());

        for (User u : chatroom.getChatroomUsers()) {
            Session s = userToSession.get(u);
            s.getAsyncRemote().sendObject(objectToSend);
        }
    }

    private void userLeftRoomNotification(Chatroom chatroom, User user){
        int chatId = chatroom.getIdNbr();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.USER_LEFT_ROOM_NOTIFICATION);
        jsonObject.put("chatId", chatId);
        jsonObject.put("name", user.getName());

        for (User u : chatroom.getChatroomUsers()) {
            Session s = userToSession.get(u);
            s.getAsyncRemote().sendObject(jsonObject);
        }
    }

    private void roomDeletedNotification(Chatroom chatroom){
        int chatId = chatroom.getIdNbr();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", MessageType.ROOM_DELETED_NOTIFICATION);
        jsonObject.put("chatId", chatId);

        for (Session s : userToSession.values()) {
            s.getAsyncRemote().sendObject(jsonObject);
        }

    }

    private void deleteRoom(Chatroom chatroom){
        int chatId = chatroom.getIdNbr();
        idToChatroom.remove(chatId);

        LOGGER.log(Level.INFO, String.format("Chat room {0} ({1}) was removed."), new Object[]{chatroom.getTitle(), chatId});

        roomDeletedNotification(chatroom);
    }

    private void joinRoom(User user, Chatroom chatroom) {
        if (chatroom.subscribeToRoom(user)) {
            newUserInChatNotification(chatroom, user);

            Session session = userToSession.get(user);
            LOGGER.log(Level.INFO, String.format("[{0}:{1}] Joined room {2} ({3})"),
                    new Object[]{session.getId(), user.getName(), chatroom.getTitle(), chatroom.getIdNbr()});
        }
    }

}
