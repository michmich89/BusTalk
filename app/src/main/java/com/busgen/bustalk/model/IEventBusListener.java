package com.busgen.bustalk.model;

import com.busgen.bustalk.events.Event;

public interface IEventBusListener {

    public void onEvent(Event event);
}
