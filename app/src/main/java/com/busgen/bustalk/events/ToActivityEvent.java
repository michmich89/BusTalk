package com.busgen.bustalk.events;


import com.busgen.bustalk.model.IServerMessage;

public class ToActivityEvent extends Event {

    public ToActivityEvent(IServerMessage serverMessage){
        super(serverMessage);
    }
}
