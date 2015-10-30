package com.busgen.bustalk.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.busgen.bustalk.model.Client;

/**
 * Class that holds everything that is basically not an activity and is run by the android os.
 */
public class MainService extends Service {

    private static MainService instance;

    private final IBinder binder = new MainBinder();
    private Client client;
    private EventBus eventBus;
    private ConnectionsHandler connectionsHandler;


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate(){

        eventBus = EventBus.getInstance();
        client = Client.getInstance();
        connectionsHandler = new ConnectionsHandler((WifiManager)getSystemService(Context.WIFI_SERVICE));
        eventBus.register(client);

    }


    @Override
    public void onDestroy(){

    }

    public class MainBinder extends Binder {

        public MainService getService() {
            return MainService.this;
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public Client getClient(){
        return client;
    }
}
