package com.busgen.bustalk.service;

import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionEstablished;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.service.EventBus;
import com.busgen.bustalk.service.JSONDecoder;
import com.busgen.bustalk.service.JSONEncoder;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander Kloutschek on 2015-10-02.
 */
public class ServerCommunicator implements IEventBusListener {
<<<<<<< HEAD
    //Kanske behöver dela upp ansvaret i flera klasser.

    private final int SECOND = 1000;
=======
>>>>>>> Remove unnecessary logs and comments
    private WebSocket webSocket;
    private WebSocketFactory factory;
    private String serverAddress;

    private final JSONDecoder jsonDecoder;
    private final JSONEncoder jsonEncoder;

    private EventBus eventBus;

    private Thread openConnectionThread;

    public ServerCommunicator(String endpointUri) {
        this.jsonEncoder = new JSONEncoder();
        this.jsonDecoder = new JSONDecoder();
        this.factory = new WebSocketFactory();
        this.serverAddress = endpointUri;
        eventBus = EventBus.getInstance();
        connect();
    }

    public void sendMessage(IServerMessage message) {
        if (webSocket != null) {
            try {
                webSocket.sendText(jsonEncoder.encode(message));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void connect() {
        openConnectionThread = new Thread(){
            @Override
            public void run() {
                try {

                    createWebsocket();
                    if(webSocket != null){
                        webSocket.connect();
                    }


                } catch (WebSocketException e) {
                    e.printStackTrace();
                }
            }
        };
        try {
            openConnectionThread.start();
            openConnectionThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        if (webSocket != null) {
            return this.webSocket.isOpen();
        } else {
            return false;
        }
    }

    public boolean canConnectToServer(){
        if(!isConnected()){
            connect();
        }
        return isConnected();
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();
        if (event instanceof ToServerEvent) {
            if (message instanceof MsgConnectionLost) {
                if(webSocket != null){
                    webSocket.disconnect();
                }
            }else {
                sendMessage(message);
            }
        }
    }

    private void createWebsocket() {
        try {
            webSocket = factory.createSocket(serverAddress);

            webSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) {
                    if (jsonDecoder.willDecode(message)) {
                        IServerMessage serverMessage = jsonDecoder.decode(message);
                        Event event = new ToClientEvent(serverMessage);
                        eventBus.postEvent(event);
                    }
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                    eventBus.postEvent(new ToServerEvent(new MsgConnectionEstablished()));
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                           WebSocketFrame clientCloseFrame, boolean closedByServer) {

                    IServerMessage connectionLost = new MsgConnectionLost();
                    eventBus.postEvent(new ToServerEvent(connectionLost));
                    eventBus.postEvent(new ToActivityEvent(connectionLost));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
