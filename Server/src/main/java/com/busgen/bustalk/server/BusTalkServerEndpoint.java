package com.busgen.bustalk.server;

import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.util.JsonDecoder;
import com.busgen.bustalk.server.util.JsonEncoder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles most of the communication related things with the client, such as when a connection is opened or
 * closed or a message is received.
 *
 * All session (connections between a user and the server) will create their own instance of this class, and uses
 * the singleton <b>BusTalkHandler</b> as their connection to their server logic.
 *
 * Created by Kristoffer on 2015-09-29.
 */
@ServerEndpoint(value = "/chat", encoders = JsonEncoder.class, decoders = JsonDecoder.class)
public class BusTalkServerEndpoint {
    private BusTalkHandler busTalkHandler;
    private static final Logger LOGGER = Logger.getLogger(BusTalkServerEndpoint.class.getName());

    public BusTalkServerEndpoint(){
        busTalkHandler = BusTalkHandler.getInstance();
    }

    @OnMessage
    public void onMessage(UserMessage userMessage, Session session){
        busTalkHandler.handleInput(userMessage, session);
    }

    @OnOpen
    public void onOpen(Session session){
        LOGGER.log(Level.INFO, String.format("[{0}] Connected to server."), session.getId());
    }

    @OnError
    public void onError(Throwable exception, Session session){
        if(exception instanceof SessionException){
            busTalkHandler.removeSession(session);
        }
    }


    @OnClose
    public void onClose(Session session){
        LOGGER.log(Level.INFO, String.format("[{0}] Disconnected from server."), session.getId());
        busTalkHandler.removeSession(session);
    }




}
