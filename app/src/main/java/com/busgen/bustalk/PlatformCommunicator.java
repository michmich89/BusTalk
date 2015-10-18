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
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Alexander Kloutschek on 2015-10-18.
 */
public class PlatformCommunicator implements IEventBusListener{

    private final int SECOND = 1000;
    private EventBus eventBus;

    public PlatformCommunicator(){
        eventBus = EventBus.getInstance();
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

    public String getNextStopData() {

        //todo Den här metoden ska brytas upp.

        JSONObject platformData = null;
        long endTime = System.currentTimeMillis();
        long durationTime = SECOND * 120;
        long startTime = endTime - durationTime;
        //Todo fixa wifi mac-address antagligen till wifi objekt och hämta rätt bussid
        //todo ersätt vin med bussid utifrån mac address

        //simulerad bussresa nedan
        String url = "https://ece01.ericsson.net:4443/ecity?dgw=Ericsson$Vin_Num_001&sensorSpec=Ericsson$Next_Stop&t1=" + startTime + "&t2=" + endTime;

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
            in.close();
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
        if(platformData == null){
            //todo bättre error
            throw new RuntimeException("platformData could not be read");
        }
        return platformData.toString();
    }

    public void onEvent(Event e){
        if(e instanceof ToServerEvent){
            IServerMessage message = e.getMessage();
            if(message instanceof MsgPlatformDataRequest){
                //Här skulle man kunna välja att hämta utifrån data type som skulle kunna finnas i requestmeddelandet.
                MsgPlatformData dataMessage = new MsgPlatformData(getNextStopData());
                ToClientEvent platformEvent = new ToClientEvent(dataMessage);
                eventBus.postEvent(platformEvent);
            }
        }
    }
}