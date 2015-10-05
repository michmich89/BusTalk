package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgNicknameAvailable implements IServerMessage {

    private String nickname;

    public MsgNicknameAvailable(String nickname){
        this.nickname = nickname;
    }
}
