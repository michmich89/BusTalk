package com.busgen.bustalk;

import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
<<<<<<< 21bac057cb9d370e2c3152fe84fd28a34769926b
import com.busgen.bustalk.model.ServerMessages.MsgConnectToServer;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionEstablished;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionStatus;
=======
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
>>>>>>> Continued on testbranch
import com.busgen.bustalk.service.EventBus;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
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

    private WebSocket webSocket;
    private WebSocketFactory factory;
    private String serverAddress;

    private final JSONDecoder jsonDecoder;
    private final JSONEncoder jsonEncoder;

    private EventBus eventBus;

    private Thread openConnectionThread;

    // endpointURI is simply a string looking something like this:
    // "ws://sandra.kottnet.net:8080/BusTalkServer/chat" (or whatever address to connect to)
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
            Log.d("MyTag", "sending message to server...");
            webSocket.sendText(jsonEncoder.encode(message));
        }
    }


    public void connect() {
        //this.webSocket.connectAsynchronously();
        openConnectionThread = new Thread(){
            @Override
            public void run() {
                try {
                    if (webSocket == null) {
                        System.out.println("Websocket was null, creating websocket...");
                        createWebsocket();
                    }
                    if (webSocket != null) {
                        System.out.println("Websocket wasn't null anymore, connecting to server");
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
            System.out.println("Websocket existed and was :" + this.webSocket.isOpen());
            return this.webSocket.isOpen();
        } else {
            System.out.println("Websocket is null!");
        }
        return false;
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage tempMessage = event.getMessage();
        Log.d("MyTag", tempMessage.toString());
        if(tempMessage instanceof MsgJoinRoom){
            //Log.d("MyTag", tempMessage.toString());
            Log.d("MyTag", "blagaha");
        }
        if (event instanceof ToServerEvent) {
            Log.d("MyTag", "Server received some sort of event");
            IServerMessage message = event.getMessage();
            if(message instanceof MsgConnectToServer){
                System.out.println("Want to connect to server");
                if(!isConnected()){
                    System.out.println("Wasn't connected to server, trying to connect...");
                    connect();
                }
                eventBus.postEvent(new ToActivityEvent(new MsgConnectionStatus(isConnected())));
            }else {
                sendMessage(message);
            }
        }
    }

    private void createWebsocket() {
        try {
            webSocket = factory.createSocket(serverAddress);
            System.out.println("Websocket created");

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
                    eventBus.postEvent(new ToServerEvent(new MsgConnectionEstablished()));
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                           WebSocketFrame clientCloseFrame, boolean closedByServer) {

                    // Do things when disconnected from server
                    //todo stoppa timer, skicka connectionlost
                    IServerMessage connectionLost = new MsgConnectionLost();
                    eventBus.postEvent(new ToServerEvent(connectionLost));
                    eventBus.postEvent(new ToClientEvent(connectionLost));
                }
            });
            System.out.println("Added listener to the websocket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}