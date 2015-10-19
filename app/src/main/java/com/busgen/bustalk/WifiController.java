package com.busgen.bustalk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.busgen.bustalk.utils.BussIDs;

import java.util.HashMap;
import java.util.List;

/**
 * Created by nalex on 19/10/2015.
 */
public class WifiController extends Service{
    private boolean isConnected;
    private WifiManager wifiManager;
    private BussIDs bussIDs;
    private HashMap<String, String> bssidToRegNr;


    public WifiController(){
        isConnected = false;
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        bussIDs = new BussIDs();
        bssidToRegNr = bussIDs.getBssidToRegNrMap();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public boolean  isConnected(){
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        for(ScanResult result : scanResultList){
            if(bssidToRegNr.containsKey(result.BSSID)){
                return true;
            }
        }
        return false;
    }

    public String getWifiName(){
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        for(ScanResult result : scanResultList){
            if(bssidToRegNr.containsKey(result.BSSID)){
                return bssidToRegNr.get(result.BSSID);
            }
        }
        return null;
    }
}
