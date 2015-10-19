package com.busgen.bustalk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.support.v7.app.AppCompatActivity;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.service.EventBus;
import com.busgen.bustalk.service.MainService;

public class BindingActivity extends AppCompatActivity implements IEventBusListener {

    EventBus eventBus = null;
    boolean isBound = false;
    MainService mainService;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventBus = EventBus.getInstance();
        Intent intent = new Intent(this, MainService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            MainService.MainBinder mainBinder = (MainService.MainBinder) binder;
            mainService = mainBinder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }
    };
@Override
    protected void onDestroy(){
    super.onDestroy();
    unbindService(serviceConnection);

    }
    @Override
    public void onEvent(Event event) {

    }
}
