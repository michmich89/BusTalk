package com.busgen.bustalk.events;


import com.busgen.bustalk.model.IServerMessage;

public class ToServerEvent extends Event {

    public ToServerEvent(IServerMessage serverMessage){
        super(serverMessage);
    }
}
