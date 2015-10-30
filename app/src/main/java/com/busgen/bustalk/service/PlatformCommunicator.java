package com.busgen.bustalk.service;

import android.util.Base64;
import android.util.Log;


import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToPlatformEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
import com.busgen.bustalk.service.EventBus;
import com.busgen.bustalk.utils.BussIDs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Class that fetches data for the different buses from the innovation platform.
 */
public class PlatformCommunicator implements IEventBusListener {

    private final int SECOND = 1000;
    private BussIDs bussIDs;
    private HashMap<String, String> regNrToDgw;
    private HashMap<String, String> bssidToRegnr;
    private String busStop;
    private EventBus eventBus;

    public PlatformCommunicator() {
        bussIDs = new BussIDs();
        regNrToDgw = bussIDs.getRegNrToDgwMap();
        bssidToRegnr = bussIDs.getBssidToRegNrMap();
        eventBus = EventBus.getInstance();

    }

    /**
     * Returns the verification string in its base64 form.
     */
    private String getLoginVerification() throws IOException {
        String userNamePass = getVerificationFromFile();
        byte[] bytePass = userNamePass.getBytes("UTF-8");
        String base64EncodedPass = Base64.encodeToString(bytePass, Base64.DEFAULT);
        return "Basic " + base64EncodedPass;
    }

    /**
     * Returns the string representing our verification to connect to the platform api.
     */
    private String getVerificationFromFile(){
        return "grp16:uRP*-F7kuD";

    }

    /**
     * Fetches a string representing the next stop.
     * @param bussID the bus or busstop you want to get the next busstop for.
     * @return string of the next stop.
     */
    public String getNextStopData(String bussID) {
        //todo Den här metoden ska brytas upp.
        if(bussID == null){
            //todo send exception?
            return "...";
        }
        busStop = null;
        String dgw = new String();
        if (regNrToDgw.containsKey(bussID)) {
            dgw = regNrToDgw.get(bussID);
        } else if(bussID.equals("Test")) {
            //this represents the simulated buss
            dgw = "Ericsson$Vin_Num_001";
        } else if(!bssidToRegnr.containsValue(bussID)){
            //means that the string is illegal and the bussid haven't been added to our bssid collection.
            return "...";
        } else {
            //This means that bussID should be a busstop and thus the busstop itself should be displayed.
            return bussID;
        }

        JSONObject platformData = null;
        long endTime = System.currentTimeMillis();
        long durationTime = SECOND * 60l;
        long startTime = endTime - durationTime;

        String url = "https://ece01.ericsson.net:4443/ecity?dgw=" + dgw + "&sensorSpec=Ericsson$Next_Stop&t1=" + startTime + "&t2=" + endTime;
        /*Streams, initializing them to null so that we can easily check
        if they have been initialized in order to close them properly
        */
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader in = null;

        try {
            String key = getLoginVerification();
            URL platformURL = new URL(url);
            HttpsURLConnection connection = (HttpsURLConnection) platformURL.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", key);

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            StringBuffer response = new StringBuffer();

            inputStream = connection.getInputStream();
            inputStreamReader = new InputStreamReader(inputStream);
            in = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println("response :" + response.toString());

            try {
                JSONArray platformArray = new JSONArray(response.toString());
                platformData = platformArray.getJSONObject(platformArray.length() - 1);
                busStop = platformData.getString("value");
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                } else if (inputStreamReader != null) {
                    inputStreamReader.close();
                } else if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (busStop == null){
            busStop = "...";
        }
        return busStop;
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();

        if (event instanceof ToPlatformEvent) {
            if (message instanceof MsgPlatformDataRequest) {
                MsgPlatformDataRequest requestMessage = (MsgPlatformDataRequest)message;
                String nextStop = getNextStopData(requestMessage.getBussID());

                MsgPlatformData newMessage = new MsgPlatformData("nextStop", nextStop);
                Event newEvent = new ToActivityEvent(newMessage);
                eventBus.postEvent(newEvent);
            }
        }
    }
}
