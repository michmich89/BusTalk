package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Alexander Kloutschek on 2015-10-18.
 */
public class MsgPlatformData implements IServerMessage{
    private String data;
    private String dataType;

    public MsgPlatformData(String dataType, String data){
        this.dataType = dataType;
        this.data = data;
    }

    public String getDataType(){
        return dataType;
    }

    public String getData(){
        return data;
    }
}
