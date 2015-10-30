package com.busgen.bustalk.service;

import android.net.wifi.WifiManager;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToPlatformEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectToServer;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionEstablished;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionStatus;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
import com.busgen.bustalk.model.ServerMessages.MsgSetGroupId;
import com.busgen.bustalk.model.ServerMessages.MsgStartPlatformTimer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that that is responsible for all the connections to the app. Has a timer that checks if we still have a connection.
 */
public class ConnectionsHandler implements IEventBusListener{
    private ServerCommunicator serverCom;
    private PlatformCommunicator platformCom;
    private WifiController wifiController;
    private boolean connectionStatus;

    private EventBus eventBus;
    private String groupID;

    private Timer timer;
    private int timeDisconnected;

    private final boolean isTest = true;
    private boolean isTimerRunning;

    public ConnectionsHandler(WifiManager wifiManager){
        super();
        timeDisconnected = 0;
        isTimerRunning = false;
        //The string in servercommunicator can be any url that has our server installed
        serverCom = new ServerCommunicator("ws://sandra.kottnet.net:8080/BusTalkServer/chat");
        platformCom = new PlatformCommunicator();
        wifiController = new WifiController(wifiManager);
        eventBus = EventBus.getInstance();
        eventBus.register(this);
        eventBus.register(serverCom);
        eventBus.register(platformCom);

    }

    /**
     * Starts timer that checks if our connection is acceptable.
     */
    private void startTimer() {
        if(!isTimerRunning) {
            System.out.println("Trying to start timer");
            this.timer = new Timer("wifiCheck");
            TimerTask timerTask = new WifiTimerTask();
            timer.scheduleAtFixedRate(timerTask, 0, 5000);
            isTimerRunning = true;
        }
    }

    private void stopTimer() {
        timer.cancel();
        timer.purge();
        isTimerRunning = false;
        System.out.println("Timer stopped");
    }

    @Override
    public void onEvent(Event e){
        if (e instanceof ToServerEvent) {
            IServerMessage message = e.getMessage();
            if (message instanceof MsgConnectionEstablished) {
            } else if (message instanceof MsgConnectionLost) {
                stopTimer();
            } else if (message instanceof MsgConnectToServer) {
                boolean couldConnect;
                if(serverCom.canConnectToServer()){
                    if(isTest){
                        couldConnect = true;
                        groupID = "Test";
                        eventBus.postEvent(new ToClientEvent(new MsgSetGroupId(groupID)));
                    }else if(wifiController.isConnected()){
                        groupID = wifiController.getWifiName();
                        eventBus.postEvent(new ToClientEvent(new MsgSetGroupId(groupID)));
                        couldConnect = true;
                    }else{
                        couldConnect = false;
                    }
                }else{
                    couldConnect = false;
                }
                eventBus.postEvent(new ToActivityEvent(new MsgConnectionStatus(couldConnect)));

            } else if (message instanceof MsgStartPlatformTimer) {
                startTimer();
            }
        }
    }

    private class WifiTimerTask extends TimerTask{
            @Override
            public void run() {
                if(serverCom.isConnected()){
                    if(isTest){
                        sendNextStopData(groupID);
                    } else if(wifiController.isConnectedTo(groupID)){
                        timeDisconnected = 0;
                        sendNextStopData(groupID);
                    }else{
                        sendNextStopData(groupID);
                        timeDisconnected = timeDisconnected + 1;
                        if(timeDisconnected >= 3){
                            sendConnectionLost();
                        }
                    }
                } else {
                    sendConnectionLost();
                }
            }

        private void sendConnectionLost(){
            eventBus.postEvent(new ToServerEvent(new MsgConnectionLost()));
        }

        private void sendNextStopData(String bussID) {
            eventBus.postEvent(new ToPlatformEvent(new MsgPlatformDataRequest(bussID)));
        }
    }
}
