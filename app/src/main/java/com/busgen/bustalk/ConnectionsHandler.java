package com.busgen.bustalk;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.service.EventBus;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nalex on 19/10/2015.
 */
public class ConnectionsHandler implements IEventBusListener{
    private ServerCommunicator serverCom;
    private PlatformCommunicator platformCom;
    private WifiController wifiController;
    private boolean connectionStatus;
    private boolean isTest;
    private EventBus eventBus;

    private Timer timer;
    private TimerTask timerTask;

    public ConnectionsHandler(){
        super();
        serverCom = new ServerCommunicator("ws://sandra.kottnet.net:8080/BusTalkServer/chat");
        platformCom = new PlatformCommunicator();
        //wifiController = new WifiController(wifiManager);
        eventBus = EventBus.getInstance();
        eventBus.register(this);

        timer = new Timer("wifiCheck");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!getConnectionStatus()) {
                    sendConnectionStatus();
                    //String wifiName = wifiController.getWifiName();
                    String wifiName = null;
                    String nextStop = platformCom.getNextStopData(wifiName);
                    sendNextStopData(nextStop);
                }
            }

            private boolean getConnectionStatus(){
                //return (isTest || wifiController.isConnected()) && serverCom.isConnected();
                return serverCom.isConnected();
            }

            private void sendConnectionStatus(){
                eventBus.postEvent(new ToClientEvent(new MsgConnectionLost()));
            }

            private void sendNextStopData(String nextStop) {
                eventBus.postEvent(new ToActivityEvent(new MsgPlatformData(nextStop)));
            }
        };

        timer.schedule(timerTask, 15000);
    }

    @Override
    public void onEvent(Event e){

    }
}
