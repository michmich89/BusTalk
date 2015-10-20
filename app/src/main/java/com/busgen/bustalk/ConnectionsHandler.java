package com.busgen.bustalk;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.events.ToPlatformEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionEstablished;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
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
        eventBus.register(serverCom);
        eventBus.register(platformCom);

        timer = new Timer("wifiCheck");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Running timertask...");
                sendNextStopData();
                if (getConnectionStatus()) {
                    //String wifiName = wifiController.getWifiName();
                    //String nextStop = platformCom.getNextStopData(wifiName);
                    //sendNextStopData(nextStop);
                } else {
                    sendConnectionStatus();
                }
            }

            private boolean getConnectionStatus(){
                //return (isTest || wifiController.isConnected()) && serverCom.isConnected();
                return serverCom.isConnected();
            }

            private void sendConnectionStatus(){
                eventBus.postEvent(new ToClientEvent(new MsgConnectionLost()));
            }

            private void sendNextStopData() {
                eventBus.postEvent(new ToPlatformEvent(new MsgPlatformDataRequest()));
            }
        };
    }

    private void startTimer() {
        timer.scheduleAtFixedRate(timerTask, 5000, 15000);
    }

    private void stopTimer() {
        timer.cancel();
    }

    @Override
    public void onEvent(Event e){
        if (e instanceof ToServerEvent) {
            IServerMessage message = e.getMessage();
            if (message instanceof MsgConnectionEstablished) {
                startTimer();
            } else if (message instanceof MsgConnectionLost) {
                stopTimer();
            }
        }
    }
}
