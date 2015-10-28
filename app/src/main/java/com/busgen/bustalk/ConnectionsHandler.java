package com.busgen.bustalk;

import android.net.wifi.WifiManager;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.events.ToPlatformEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgConnectToServer;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionEstablished;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionLost;
import com.busgen.bustalk.model.ServerMessages.MsgConnectionStatus;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
import com.busgen.bustalk.model.ServerMessages.MsgSetGroupId;
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

    private EventBus eventBus;
    private String groupID;

    private Timer timer;
    private TimerTask timerTask;
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

        //timer = new Timer("wifiCheck");
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if(serverCom.isConnected()){
                System.out.println("Running timertask...");
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
                eventBus.postEvent(new ToActivityEvent(new MsgConnectionLost()));
                eventBus.postEvent(new ToServerEvent(new MsgConnectionLost()));
            }

            private void sendNextStopData(String bussID) {
                eventBus.postEvent(new ToPlatformEvent(new MsgPlatformDataRequest(bussID)));
            }
        };
    }

    private void startTimer() {
        if(!isTimerRunning) {
            this.timer = new Timer("wifiCheck");
            timer.scheduleAtFixedRate(timerTask, 0, 5000);
            isTimerRunning = true;
        }
    }

    private void stopTimer() {
        timer.cancel();
        isTimerRunning = false;
    }

    @Override
    public void onEvent(Event e){
        if (e instanceof ToServerEvent) {
            IServerMessage message = e.getMessage();
            if (message instanceof MsgConnectionEstablished) {
                System.out.println("CONNECTION ESTABLISHED");
            } else if (message instanceof MsgConnectionLost) {
                System.out.println("CONNECTION LOST");
                stopTimer();
            } else if (message instanceof MsgConnectToServer) {
                boolean couldConnect;
                if(serverCom.canConnectToServer()){
                    if(isTest){
                        couldConnect = true;
                        groupID = "Test";
                        eventBus.postEvent(new ToClientEvent(new MsgSetGroupId(groupID)));
                        startTimer();
                    }else if(wifiController.isConnected()){
                        groupID = wifiController.getWifiName();
                        eventBus.postEvent(new ToClientEvent(new MsgSetGroupId(groupID)));
                        couldConnect = true;
                        startTimer();
                    }else{
                        couldConnect = false;
                    }
                }else{
                    couldConnect = false;
                }
                eventBus.postEvent(new ToActivityEvent(new MsgConnectionStatus(couldConnect)));

            }
        }
    }
}
