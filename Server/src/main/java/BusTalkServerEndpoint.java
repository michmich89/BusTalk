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
public class BusTalkServerEndpoint {
    private BusTalkHandler busTalkHandler;
    
    public BusTalkServerEndpoint(){
        busTalkHandler = BusTalkHandler.getInstance();
    }

    @OnMessage
    public void onMessage(UserMessage userMessage, Session session){
        busTalkHandler.handleInput(userMessage, session);
    }

    @OnOpen
    public void onOpen(Session session){
    //    LOGGER.log(Level.INFO, String.format("[{0}] Connected to server.", session.getId()));
    }

    @OnError
    public void onError(Throwable exception, Session session){

    }


    @OnClose
    public void onClose(Session session){
    //    LOGGER.log(Level.INFO, String.format("[{0}] Disconnected from server."));
    }




}
