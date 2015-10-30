package com.busgen.bustalk.service;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import com.busgen.bustalk.utils.BussIDs;

import java.util.HashMap;
import java.util.List;

/**
 * Class that checks if the machine running the application is in physical range of an accepted wifi-address.
 * You can also get the accepted wifi address if the machine in fact is in range.
 */
public class WifiController{
    private boolean isConnected;
    private WifiManager wifiManager;
    private BussIDs bussIDs;
    private HashMap<String, String> bssidToRegNr;
    private Context context;

    public WifiController(WifiManager wifiManager) {
        isConnected = false;
        this.wifiManager = wifiManager;
        bussIDs = new BussIDs();
        bssidToRegNr = bussIDs.getBssidToRegNrMap();
    }

    public boolean isConnectedTo(String groupID){
        List<ScanResult> scanResultList = wifiManager.getScanResults();
        for(ScanResult result : scanResultList){
            if(bssidToRegNr.containsKey(result.BSSID)){
                if(groupID.equals(bssidToRegNr.get(result.BSSID))){
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isConnected(){
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
