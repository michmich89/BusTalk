import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kristoffer on 2015-09-29.
 */



@ServerEndpoint(value = "/chat", encoders = BusTalkJsonEncoder.class, decoders = BusTalkJsonDecoder.class)
public class BusTalkServer {

    //private InputHandler inputHandler;
    private final Map<Integer, Chatroom> idToChatroom;
    private final BiMap<User, Session> userToSession;
    private final List<String> disallowedNames;
    private final Logger LOGGER;
    private final ChatroomFactory chatroomFactory;


    public BusTalkServer(){
    //    inputHandler = new InputHandler();
        idToChatroom = new HashMap<Integer, Chatroom>();
        userToSession = HashBiMap.create();
        disallowedNames = new ArrayList<String>();

        disallowedNames.add("Alexander Kloutschek"); //TIHI
        LOGGER = Logger.getLogger(BusTalkServer.class.getName());
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

    @OnMessage
    public void onMessage(UserMessage userMessage, Session session){
        handleInput(userMessage, session);
    }

    @OnOpen
    public void onOpen(Session session){
        LOGGER.log(Level.INFO, session.getId() + " has conntected");
    }

    @OnError
    public void onError(){

    }


    @OnClose
    public void onClose(Session session){

    }


    //Öppna på egen risk...
    private void handleInput(UserMessage userMessage, Session session){
        //Du va när den sist - ditt ansvar!
        try {
            User sender = new User();
            int type = userMessage.getInt("type");

            /* The UserMessage is telling the program that it's a...
            11 - Chat message

            21 - Create Room request
            22 - Join Room request
            23 - List of user in room request
            26 - Get all chat rooms
            29 - Leave Room request

            31 - Choose Nick request
            32 - Nick Available check
             */

            switch(type){
                case 11:
                    break;

                case 21: // Create room
                {
                    String nameOfRoom = userMessage.getString("chatName");
                    Chatroom chatroom = chatroomFactory.createChatroom(nameOfRoom);
                    int chatId = chatroom.getIdNbr();

                    idToChatroom.put(chatId, chatroom);
                    chatroom.subscribeToRoom(session);

                }
                    break;
                case 22: // Join room
                {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatRoom = idToChatroom.get(chatId);
                    chatRoom.subscribeToRoom(session);

                    for (Session s : chatRoom.getChatroomUsers()) {
                        //TODO: Vad ska skickas tillbaka? Anrop för att meddela berörda användare
                    }
                }
                    break;
                case 23:
                    break;
                case 26:
                    break;
                case 29: // Leave room
                {
                    int chatId = userMessage.getInt("chatId");
                    Chatroom chatroom = idToChatroom.get(chatId);

                    chatroom.unsubscribeToRoom(session);
                    if(chatroom.getChatroomUsers().isEmpty() && chatroom.getIdNbr() > 100){
                        idToChatroom.remove(chatId);
                    }
                }

                    break;
                case 31:
                    break;
                case 32:
                    break;

                default:

            }

            if (type.equals("chat message")) {
            } else if (type.equals("join room")) {
            } else if (type.equals("leave room")) {
            } else if (type.equals("create room")) {
            } else if (type.equals("get users in room")) {
                int chatId = userMessage.getInt("chatId");
                Chatroom chatRoom = idToChatroom.get(chatId);
                chatRoom.getChatroomUsers();

                /*
                TODO: Vad ska skickas tillbaka?
                 */


            } else if (type.equals("set credentials")) {
                String newNickName = userMessage.getString("name");
                String newInterest = userMessage.getString("interests");

                if (!disallowedNames.contains(newNickName)){

                    //DOES THE USER EXIST? IF NOT - DO THIS
                    if(!userToSession.containsValue(session)) {
                        User user = new User(newNickName, newInterest);
                        addUser(user, session);
                        LOGGER.log(Level.INFO, "A user named " + user.getName() + " has been created for session with ID: " + session.getId());

                    //THE USER EXIST - DO THIS
                    //TODO: Maybe this should check if new interests = null and then leave the interests as they are?
                    }else{
                        User user = userToSession.inverse().get(session);

                        //BEGONE WITH THE OLD
                        String oldName = user.getName();
                        removeDisallowedName(oldName);

                        //...IN WITH THE NEW
                        user.setName(newNickName);
                        user.setInterests(newInterest);
                        addDisallowedName(newNickName);

                        LOGGER.log(Level.INFO, session.getId() + ": changed name from " + oldName + "to " + newNickName
                                    + " and interest to " + newInterest);

                    }
                    /*
                    TODO: Se till att två användare inte kan ha samma namn, och meddela användaren om det valt ett
                    namn som är upptaget. Vad händer om en användare försöker byta till samma nick?
                     */
                /*
                Här tar vi hand om situationen då en användare försöker byta till samma namn
                Vi plockar ut användaren
                Kollar om denna användares namn överensstämmer med det nya smeknamnet, till exempel då han vill ha en
                stor bokstav i mitten som en jävla chef, eller helt enkelt vill ha kvar namnet och enbart byta intressen

                 */
                }else if(userToSession.inverse().get(session).getName().equalsIgnoreCase(newNickName)){ //Urgh
                    User user = userToSession.inverse().get(session);
                    //BEGONE WITH THE OLD
                    String oldName = user.getName();
                    removeDisallowedName(oldName);

                    //...IN WITH THE NEW
                    user.setName(newNickName);
                    user.setInterests(newInterest);
                    addDisallowedName(newNickName);
                }
            }
        }catch(IllegalArgumentException e){
            //TODO: Vi ska skicka tillbaka information om Vad som gick fel
            session.getAsyncRemote().sendText(e.getMessage());
        }
    }

    private void addDisallowedName(String name){
        if(!disallowedNames.contains(name)) {
            disallowedNames.add(name);
        }
    }

    private void removeDisallowedName(String name){
        //if(disallowedNames.contains(name)){
            disallowedNames.remove(name);
        //}
    }

    private void addUser(User user, Session session){
        userToSession.put(user, session);
        disallowedNames.add(user.getName());
    }

    private void removeUser(User user){
        disallowedNames.remove(user.getName());
        userToSession.remove(user);
    }

    private void newUserInChat(){
        JSONObject objectToSend = new JSONObject();
        objectToSend.put("type", 123456789);
        objectToSend.put("chatId", );
        objectToSend.put("user", );
        objectToSend.put("interest",);
    }
}