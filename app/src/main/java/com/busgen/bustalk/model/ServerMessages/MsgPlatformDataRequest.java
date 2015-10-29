package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Alexander Kloutschek on 2015-10-18.
 */
public class MsgPlatformDataRequest implements IServerMessage{
    private String bussID;
    //här skulle man kunna beskriva vilken sorts data det är man vill skicka kanske?
    private String dataType;

    public MsgPlatformDataRequest(String bussID){
        this.bussID = bussID;
    }

    public String getBussID(){
        return bussID;
    }
}
