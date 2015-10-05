package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgNicknameAvailable implements IServerMessage {

    private boolean availability;

    public MsgNicknameAvailable(boolean availability){
        this.availability = availability;
    }

    public boolean getAvailability(){
        return availability;
    }
}
