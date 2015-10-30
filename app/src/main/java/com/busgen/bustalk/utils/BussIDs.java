package com.busgen.bustalk.utils;

import java.util.HashMap;

/**
 * BussIDS contains two collections. The first one is used to translate bssids to their registration number if it's a buss or
 * to its name if it's a busstop. The second one converts the name of the reg number of a bus to the signal name used when
 * fetching data from the innovation platform.
 */
public class BussIDs {
    private HashMap<String, String> bssidToRegNr;
    private HashMap<String, String> regNrToDgw;

    public BussIDs(){
        bssidToRegNr = new HashMap<String, String>();

        //buses
        bssidToRegNr.put("04:f0:21:10:0a:07", "EPO 131");
        bssidToRegNr.put("04:f0:21:10:09:df", "EPO 136");
        bssidToRegNr.put("04:f0:21:10:09:e8", "EPO 143");
        bssidToRegNr.put("04:f0:21:10:09:b8", "EOG 604");
        bssidToRegNr.put("04:f0:21:10:09:e7", "EOG 606");
        bssidToRegNr.put("04:f0:21:10:09:5b", "EOG 616");
        bssidToRegNr.put("04:f0:21:10:09:53", "EOG 627");
        bssidToRegNr.put("04:f0:21:10:09:53", "EOG 622");
        bssidToRegNr.put("04:f0:21:10:09:b7", "EOG 634");

        //bus stops
        bssidToRegNr.put("06:f0:21:10:0c:87","Chalmersplatsen");
        bssidToRegNr.put("06:f0:21:10:0c:ab", "Chalmersplatsen");
        bssidToRegNr.put("06:f0:21:11:5c:3d", "Teknikgatan");


        regNrToDgw = new HashMap<String, String>();

        regNrToDgw.put("EPO 131", "Ericsson$100020");
        regNrToDgw.put("EPO 136", "Ericsson$100021");
        regNrToDgw.put("EPO 143", "Ericsson$100022");
        regNrToDgw.put("EOG 604", "Ericsson$171164");
        regNrToDgw.put("EOG 606", "Ericsson$171234");
        regNrToDgw.put("EOG 616", "Ericsson$171235");
        regNrToDgw.put("EOG 622", "Ericsson$171327");
        regNrToDgw.put("EOG 627", "Ericsson$171328");
        regNrToDgw.put("EOG 634", "Ericsson$171330");
    }

    public HashMap<String, String> getBssidToRegNrMap(){
        return bssidToRegNr;
    }

    public HashMap<String, String> getRegNrToDgwMap(){
        return regNrToDgw;
    }

}
