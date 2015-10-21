package com.busgen.bustalk;

import android.util.Base64;
import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToActivityEvent;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToPlatformEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
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
 * Created by Alexander Kloutschek on 2015-10-18.
 */
public class PlatformCommunicator implements IEventBusListener {

    private final int SECOND = 1000;
    private BussIDs bussIDs;
    private HashMap<String, String> regNrToDgw;
    private String busStop;
    private EventBus eventBus;

    public PlatformCommunicator() {
        bussIDs = new BussIDs();
        regNrToDgw = bussIDs.getRegNrToDgwMap();
        eventBus = EventBus.getInstance();

    }

    private String getLoginVerification() throws IOException {
        String userNamePass = getVerificationFromFile();
        byte[] bytePass = userNamePass.getBytes("UTF-8");
        String base64EncodedPass = Base64.encodeToString(bytePass, Base64.DEFAULT);
        return "Basic " + base64EncodedPass;
    }

    private String getVerificationFromFile() throws IOException {
        /*String filePath = new String("app/src/main/res/InnovationPlatformVerification.txt");
        File apiKeyFile = new File(filePath);
        String apiKey = Files.toString(apiKeyFile, Charset.UTF_8);
        URL fileURL = Resources.getResource("InnovationPlatformVerification.txt");
        String apiKey = Resources.toString(fileURL, Charsets.UTF_8);
        return apiKey;*/
        return "grp16:uRP*-F7kuD";

    }

    public String getNextStopData(String bussID) {
        //todo Den här metoden ska brytas upp.
        busStop = null;
        String dgw = new String();
        if (regNrToDgw.containsValue(bussID)) {
            dgw = regNrToDgw.get(bussID);
        } else if (bussID != null) {
            //hållplats
            return bussID;
        } else {

            //If we don't have a legal bussID, the simulated buss will be used.
            dgw = "Ericsson$Vin_Num_001";
        }

        JSONObject platformData = null;
        long endTime = System.currentTimeMillis();
        long durationTime = SECOND * 60l;
        long startTime = endTime - durationTime;

        //Todo fixa wifi mac-address antagligen till wifi objekt och hämta rätt bussid
        //todo ersätt vin med bussid utifrån mac address

        //simulerad bussresa nedan
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=" + dgw + "&sensorSpec=Ericsson$Next_Stop&t1=" + startTime + "&t2=" + endTime;
        //System.out.println(url);
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
            //System.out.println("while :" + inputLine);
            //System.out.println("response :" + response.toString());

            try {
                //String teststring = response.substring(1,response.length()-1);
                //System.out.println(teststring);
                JSONArray platformArray = new JSONArray(response.toString());
                platformData = platformArray.getJSONObject(platformArray.length() - 1);
                busStop = platformData.getString("value");

            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException ex) {
            //todo needs proper exception handling and probably display an error message in the app
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
        //todo hantera null om servern inte skickar meddelande
        if (busStop == null){
            busStop = "NisseTerminalen";
        }
        return busStop;
    }

    @Override
    public void onEvent(Event event) {
        IServerMessage message = event.getMessage();

        if (event instanceof ToPlatformEvent) {
            Log.d("MyLog", "platformevent");
            /* Skickar endast nästa hållplats tills vidare*/
            if (message instanceof MsgPlatformDataRequest) {
                String nextStop = getNextStopData(null);
                System.out.println("Nextstop = " + nextStop);

                MsgPlatformData newMessage = new MsgPlatformData("nextStop", nextStop);
                Event newEvent = new ToActivityEvent(newMessage);
                eventBus.postEvent(newEvent);
            }
        }
    }
}
