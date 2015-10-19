package com.busgen.bustalk;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.model.IEventBusListener;

/**
 * Created by nalex on 19/10/2015.
 */
public class ConnectionsThread extends Thread implements IEventBusListener{
    private ServerCommunicator serverCom;
    private PlatformCommunicator platformCom;
    private WifiController wifiController;
    private boolean connectionStatus;

    public ConnectionsThread(){
        super();
        serverCom = new ServerCommunicator("ws://sandra.kottnet.net:8080/BusTalkServer/chat");
        platformCom = new PlatformCommunicator();
        wifiController = new WifiController();
    }

    public void sendConnectionStatus(){

    }

    public void updateConnectionStatus(){

    }

    public void onEvent(Event e){

    }



}
