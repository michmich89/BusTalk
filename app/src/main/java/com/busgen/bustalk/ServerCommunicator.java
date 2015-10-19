package com.busgen.bustalk;

import android.os.AsyncTask;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
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

    private WebSocket webSocket;
    private WebSocketFactory factory;
    private String serverAddress;

    private final JSONDecoder jsonDecoder;
    private final JSONEncoder jsonEncoder;

    // endpointURI is simply a string looking something like this:
    // "ws://sandra.kottnet.net:8080/BusTalkServer/chat" (or whatever address to connect to)
    public ServerCommunicator(String endpointUri) {
        this.jsonEncoder = new JSONEncoder();
        this.jsonDecoder = new JSONDecoder();
        this.factory = new WebSocketFactory();
        this.serverAddress = endpointUri;
        new Thread(new CreateWebSocketThread()).start();
    }

    public void sendMessage(IServerMessage message) {
        if (webSocket != null) {
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
            IServerMessage message = event.getMessage();
            sendMessage(message);
            /*
            if (message instanceof MsgChatMessage) {

            } else if (message instanceof MsgChooseNickname) {

            } else if (message instanceof MsgChooseNickname) {

            } else if (message instanceof MsgCreateRoom) {

            } else if (message instanceof MsgJoinRoom) {

            } else if (message instanceof MsgLeaveRoom) {

            } else if (message instanceof MsgLostChatRoom) {

            } else if (message instanceof MsgLostUserInChat) {

            } else if (message instanceof MsgNewChatRoom) {

            } else if (message instanceof MsgNewUserInChat) {

            } else if (message instanceof MsgNicknameAvailable) {
            }*/
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
                        if (jsonDecoder.willDecode(message)) { // Maybe it's possible to skip the whole willDecode()
                            IServerMessage serverMessage = jsonDecoder.decode(message);
                        }
                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) {
                        // Do things when connection is established
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame,
                                               WebSocketFrame clientCloseFrame, boolean closedByServer) {
                        webSocket = null;
                        // Do things when disconnected from server
                    }
                });

                connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
