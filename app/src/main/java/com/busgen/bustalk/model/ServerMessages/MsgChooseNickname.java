package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by Johan on 2015-10-05.
 */
public class MsgChooseNickname implements IServerMessage {

    private String nickname;
    private String interests;

    public MsgChooseNickname(String nickname, String interests){
        this.nickname = nickname;
        this.interests = interests;
    }

    public String getNickname(){
        return nickname;
    }

    public String getInterests(){
        return interests;
    }
}
