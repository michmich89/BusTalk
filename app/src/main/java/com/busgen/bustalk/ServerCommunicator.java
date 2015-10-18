package com.busgen.bustalk;

import android.util.Base64;

import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.service.EventBus;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;

/**
 * Created by Alexander Kloutschek on 2015-10-02.
 */
@ClientEndpoint(encoders = JSONEncoder.class, decoders = JSONDecoder.class)
public class ServerCommunicator implements IEventBusListener {
    //Kanske behöver dela upp ansvaret i flera klasser.

    private Session session;
    private EventBus eventBus;

    // URI is just simply new URI("ws://sandra.kottnet.net:8080/BusTalkServer/chat") (or whatever address to connect to)
    public ServerCommunicator(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, endpointURI);
        } catch (Exception e) { // Don't know what exception to expect...
            e.printStackTrace();
        }
        eventBus = EventBus.getInstance();
        eventBus.register(this);

    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
    }

    @OnClose
    public void onClose(Session session) {
        this.session = null;
    }

    @OnMessage
    public void onMessage(IServerMessage message) {
        ToClientEvent serverEvent = new ToClientEvent(message);
        eventBus.postEvent(serverEvent);
    }

    public void sendMessage(IServerMessage message) {
        this.session.getAsyncRemote().sendObject(message);
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
}
