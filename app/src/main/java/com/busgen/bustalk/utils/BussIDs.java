package com.busgen.bustalk.utils;

import java.util.HashMap;

/**
 * Created by nalex on 19/10/2015.
 */
public class BussIDs {
    private HashMap<String, String> bssidToRegNr;
    private HashMap<String, String> regNrToDw;

    public BussIDs(){
        bssidToRegNr = new HashMap<String, String>();
        bssidToRegNr.put("04:f0:21:10:0a:07", "EPO 131");
        bssidToRegNr.put("04:f0:21:10:09:df", "EPO 136");
        bssidToRegNr.put("04:f0:21:10:09:e8", "EPO 143");
        bssidToRegNr.put("04:f0:21:10:09:b8", "EOG 604");
        bssidToRegNr.put("04:f0:21:10:09:e7", "EOG 606");
        bssidToRegNr.put("04:f0:21:10:09:5b", "EOG 616");
        bssidToRegNr.put("04:f0:21:10:09:53", "EOG 627");
        bssidToRegNr.put("04:f0:21:10:09:e7", "EOG 606");
        bssidToRegNr.put("04:f0:21:10:09:b7", "EOG 643");

        //bus stops
        bssidToRegNr.put("06:f0:21:10:0c:87","Chalmersplatsen");
        bssidToRegNr.put("06:f0:21:10:0c:ab", "Chalmersplatsen");
        bssidToRegNr.put("06:f0:21:11:5c:3d", "Teknikgatan");


    }

    public HashMap<String, String> getBssidToRegNrMap(){
        return bssidToRegNr;
    }

}
