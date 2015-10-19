package com.busgen.bustalk;

import android.util.Base64;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToClientEvent;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformData;
import com.busgen.bustalk.model.ServerMessages.MsgPlatformDataRequest;
import com.busgen.bustalk.service.EventBus;
import com.busgen.bustalk.utils.BussIDs;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

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
public class PlatformCommunicator{

    private final int SECOND = 1000;
    private EventBus eventBus;
    private BussIDs bussIDs;
    private HashMap<String, String> regNrToDgw;

    public PlatformCommunicator(){
        eventBus = EventBus.getInstance();
        bussIDs = new BussIDs();
        regNrToDgw = bussIDs.getRegNrToDgwMap();
    }

    private String getLoginVerification() throws IOException {
        String userNamePass = getVerificationFromFile();
        byte[] bytePass = userNamePass.getBytes("UTF-8");
        String base64EncodedPass = Base64.encodeToString(bytePass, Base64.DEFAULT);
        System.out.println(base64EncodedPass);
        return base64EncodedPass;
    }

    private String getVerificationFromFile() throws IOException{
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
        String dgw = new String();
        if(regNrToDgw.containsValue(bussID)){
            dgw = regNrToDgw.get(bussID);
        }else if( bussID != null){
            //hållplats
            return bussID;
        }else{
            //If we don't have a legal bussID, the simulated buss will be used.
            dgw = "Ericsson$Vin_Num_001";
        }

        JSONObject platformData = null;
        long endTime = System.currentTimeMillis();
        long durationTime = SECOND * 5;
        long startTime = endTime - durationTime;
        //Todo fixa wifi mac-address antagligen till wifi objekt och hämta rätt bussid
        //todo ersätt vin med bussid utifrån mac address

        //simulerad bussresa nedan
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
            while (in.readLine() != null) {
                inputLine = in.readLine();
                response.append(inputLine);
            }
            System.out.println(response.toString());

            try {
                platformData = new JSONObject(response.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (IOException ex) {
            //todo needs proper exception handling and probably display an error message in the app
            ex.printStackTrace();
        }
        finally {
            try{
                if (in != null){
                    in.close();
                }else if(inputStreamReader != null){
                    inputStreamReader.close();
                }else if(inputStream != null){
                    inputStream.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        return platformData.toString();
    }
}
