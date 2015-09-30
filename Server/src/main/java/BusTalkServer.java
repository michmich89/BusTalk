import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kristoffer on 2015-09-29.
 */



@ServerEndpoint("/chat"), encoders = , decoders =
public class BusTalkServer {

    //private InputHandler inputHandler;
    private final Map<Integer, Chatroom> idToChatroom;
    private final Map<User, Session> userToSession;
    private final List<String> disallowedNames;


    public BusTalkServer(){
    //    inputHandler = new InputHandler();
        idToChatroom = new HashMap<Integer, Chatroom>();
        userToSession = new HashMap<User, Session>();
        disallowedNames = new ArrayList<String>();

        disallowedNames.add("Alexander Kloutschek"); //TIHI
    }

    @OnMessage
    public void onMessage(Session session, UserMessage userMessage){
        handleInput(userMessage, session);
    }

    @OnOpen
    public void onOpen(Session session){

    }

/*    @OnError
    public void onError(){

    }*/


    @OnClose
    public void onClose(Session session){

    }

    private void handleInput(UserMessage userMessage, Session session){
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
                String nickName = userMessage.getString("name");

                if (!disallowedNames.contains(nickName)){
                    if(){

                    }

                    /*
                    TODO: Se till att två användare inte kan ha samma namn, och meddela användaren om det valt ett
                    namn som är upptaget.
                     */


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




}
