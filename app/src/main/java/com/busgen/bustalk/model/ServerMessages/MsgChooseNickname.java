package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgChooseNickname implements IServerMessage {

    private String nickname;

    public MsgChooseNickname(String nickname){
        this.nickname = nickname;
    }
}
