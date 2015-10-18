package com.busgen.bustalk.model.ServerMessages;

/**
 * Created by Alexander Kloutschek on 2015-10-18.
 */
public class MsgPlatformData {
    private String data;
    private String dataType;

    public MsgPlatformData(String data){
        this.data = data;
    }

    public String getData(){
        return data;
    }
}
