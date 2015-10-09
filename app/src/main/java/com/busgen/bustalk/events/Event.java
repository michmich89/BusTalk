package com.busgen.bustalk.events;

import com.busgen.bustalk.model.IServerMessage;

public class Event {

    private IServerMessage serverMessage;

    public Event(IServerMessage serverMessage){
        this.serverMessage = serverMessage;
    }

    public IServerMessage getMessage() {
        return serverMessage;
    }
}
