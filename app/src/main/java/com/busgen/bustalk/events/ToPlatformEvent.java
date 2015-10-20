package com.busgen.bustalk.events;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-20.
 */
public class ToPlatformEvent extends Event {

    public ToPlatformEvent(IServerMessage serverMessage) {
        super(serverMessage);
    }
}

