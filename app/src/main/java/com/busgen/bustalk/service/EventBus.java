package com.busgen.bustalk.service;

import android.util.Log;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.model.IEventBusListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Eventbus is a singleton that is used to send messages to different classes that implements the Eventlistener inteface.
 */
public class EventBus {


    private static EventBus eventBus = null;
    private List<IEventBusListener> subscribers;


    private EventBus(){
        subscribers = new ArrayList<IEventBusListener>();
    }

    public static EventBus getInstance(){
        if(eventBus == null){
            eventBus = new EventBus();
        }return eventBus;
    }

    public void register(IEventBusListener subscriber) {
        if (!subscribers.contains(subscriber)) {
            this.subscribers.add(subscriber);
        }
    }

    public void unRegister(IEventBusListener subscriber){
        if (subscribers.contains(subscriber)){
            this.subscribers.remove(subscriber);
        }
    }

    public void postEvent(Event event){
        for (int i = 0; i < subscribers.size(); i++){
            subscribers.get(i).onEvent(event);
        }

    }

    public void clearSubscribers() {
        if (subscribers != null) {
            subscribers.clear();
        }
    }


}
