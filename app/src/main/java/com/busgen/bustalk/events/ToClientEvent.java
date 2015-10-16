package com.busgen.bustalk.events;


import com.busgen.bustalk.model.IServerMessage;

public class ToClientEvent extends Event {

    public ToClientEvent(IServerMessage serverMessage){
        super(serverMessage);
    }

}
