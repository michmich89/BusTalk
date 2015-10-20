package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by nalex on 20/10/2015.
 */
public class MsgConnectionStatus implements IServerMessage{
    private boolean isConnected;

    public MsgConnectionStatus(boolean isConnected){
        this.isConnected = isConnected;
    }

    public boolean isConnected(){
        return isConnected;
    }
}
