package com.busgen.bustalk;

import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectToServer;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionStatus;
import com.busgen.bustalk.service.EventBus;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Alexander Kloutschek on 2015-10-02.
 */
public class ServerCommunicator implements IEventBusListener {
    //Kanske behöver dela upp ansvaret i flera klasser.

    private final int SECOND = 1000;
    private WebSocket webSocket;
    private WebSocketFactory factory;
    private String serverAddress;

    private final JSONDecoder jsonDecoder;
    private final JSONEncoder jsonEncoder;

    private EventBus eventBus;

    // endpointURI is simply a string looking something like this:
    // "ws://sandra.kottnet.net:8080/BusTalkServer/chat" (or whatever address to connect to)
    public ServerCommunicator(String endpointUri) {
        this.jsonEncoder = new JSONEncoder();
        this.jsonDecoder = new JSONDecoder();
        this.factory = new WebSocketFactory();
        this.serverAddress = endpointUri;
        new Thread(new CreateWebSocketThread()).start();
        eventBus = EventBus.getInstance();
    }

    public void sendMessage(IServerMessage message) {
        if (webSocket != null) {
            Log.d("MyTag", "sending message to server...");
            webSocket.sendText(jsonEncoder.encode(message));
        }
    }


    public void connect() {
        this.webSocket.connectAsynchronously();
    }

    public boolean isConnected() {
        return this.webSocket.isOpen();
    }




    @Override
    public void onEvent(Event event) {

        if (event instanceof ToServerEvent) {
            Log.d("MyTag", "Server received some sort of event");
            IServerMessage message = event.getMessage();
            if(message instanceof MsgConnectToServer){
               if(!isConnected()){
                   connect();
                   //Thread.join()
               }
                eventBus.postEvent(new ToActivityEvent(new MsgConnectionStatus(isConnected())));
            }else {
                sendMessage(message);
            }
        }
    }

    private class CreateWebSocketThread implements Runnable {

        @Override
        public void run() {
            try {
                webSocket = factory.createSocket(serverAddress);

                webSocket.addListener(new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String message) {
                        // Handle incoming messages (decode them and such)
                        Log.d("MyTag", "" + "Receiving decodable(?) message from server...");
                        if (jsonDecoder.willDecode(message)) { // Maybe it's possible to skip the whole willDecode()
                            Log.d("MyTag", "" + "Receiving decodable message from server...");
                            IServerMessage serverMessage = jsonDecoder.decode(message);
                            Event event = new ToClientEvent(serverMessage);
                            eventBus.postEvent(event);

                        }
                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                        // Do things when connection is established
                        //todo starta timer sen
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                               WebSocketFrame clientCloseFrame, boolean closedByServer) {

                        // Do things when disconnected from server
                        //todo stoppa timer, skicka connectionlost
                    }
                });

                connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
