package com.busgen.bustalk.server;

import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.util.JsonDecoder;
import com.busgen.bustalk.server.util.JsonEncoder;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by Kristoffer on 2015-09-29.
 */



@ServerEndpoint(value = "/chat", encoders = JsonEncoder.class, decoders = JsonDecoder.class)
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
