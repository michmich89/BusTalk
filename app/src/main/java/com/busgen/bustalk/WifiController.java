package com.busgen.bustalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by nalex on 19/10/2015.
 */
public class WifiController extends Service{
    private boolean isConnected;
    private WifiManager wifiManager;

    public WifiController(){
        isConnected = false;
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void getScanResults(){
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        for(scanResultList : )


    }


}
