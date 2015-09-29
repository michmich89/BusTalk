import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kristoffer on 2015-09-29.
 */



@ServerEndpoint("/chat"), encoders = , decoders =
public class BusTalkServer {

    //private InputHandler inputHandler;
    private final Map<Integer, Chatroom> idToChatroom;
    private final Map<User, Session> userToSession;


    public BusTalkServer(){
    //    inputHandler = new InputHandler();
        idToChatroom = new HashMap<Integer, Chatroom>();
        userToSession = new HashMap<User, Session>();
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

        if(userMessage.getType().equals("chat message")){
            int chatId = userMessage.getChatID();
            Chatroom chatRoom = idToChatroom.get(chatId);
            String message = userMessage.getMessage();

            for(Session s : chatRoom.getChatroomUsers()){
                s.getAsyncRemote().sendText(message);
            }

        }else if(userMessage.getType().equals("join room")){
            int chatId = userMessage.getChatID();
            Chatroom chatRoom = idToChatroom.get(chatId);
            chatRoom.subscribeToRoom(session);

            for(Session s : chatRoom.getChatroomUsers()){
                s.getAsyncRemote().send
            }


        }else if(userMessage.getType().equals("leave room")) {
            int chatId = userMessage.getChatID();
            Chatroom chatRoom = idToChatroom.get(chatId);
            chatRoom.unsubscribeToRoom(session);


        }else if(userMessage.getType().equals("create room")){



        }else if(userMessage.getType().equals("get users in room")){
            int chatId = userMessage.getChatID();
            Chatroom chatRoom = idToChatroom.get(chatId);
            chatRoom.getChatroomUsers();


        }else if(userMessage.getType().equals("set credentials")){
            if(){
               User user = new User()


            }


        }

    }




}
