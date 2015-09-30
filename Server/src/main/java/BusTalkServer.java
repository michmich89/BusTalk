import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


    public BusTalkServer(){
    //    inputHandler = new InputHandler();
        idToChatroom = new HashMap<Integer, Chatroom>();
        userToSession = HashBiMap.create();
        disallowedNames = new ArrayList<String>();

        disallowedNames.add("Alexander Kloutschek"); //TIHI
        LOGGER = Logger.getLogger(BusTalkServer.class.getName());

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
            String type = userMessage.getString("type");

            if (type.equals("chat message")) {
                int chatId = userMessage.getInt("chatId");
                Chatroom chatRoom = idToChatroom.get(chatId);
                String message = userMessage.getString("message");

                for (Session s : chatRoom.getChatroomUsers()) {
                    //TODO: Vad ska skickas tillbaka?

                    //s.getAsyncRemote().sendText(message);
                }

            } else if (type.equals("join room")) {
                int chatId = userMessage.getInt("chatId");
                Chatroom chatRoom = idToChatroom.get(chatId);
                chatRoom.subscribeToRoom(session);

                for (Session s : chatRoom.getChatroomUsers()) {
                    //TODO: Vad ska skickas tillbaka?
                }


            } else if (type.equals("leave room")) {
                int chatId = userMessage.getInt("chatId");
                Chatroom chatRoom = idToChatroom.get(chatId);
                chatRoom.unsubscribeToRoom(session);


            } else if (type.equals("create room")) {
                /*
                TODO: ChatroomFactory, se till att rum skapas, vad ska skickas tillbaka, se till att "skaparen"
                kommer med i rummet och att andra användare som Bör kunna se rummet får möjlighet till det
                 */


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
}