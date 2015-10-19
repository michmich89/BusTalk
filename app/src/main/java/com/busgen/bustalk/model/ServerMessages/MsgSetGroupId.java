package com.busgen.bustalk.model.ServerMessages;

import com.busgen.bustalk.model.IServerMessage;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgSetGroupId implements IServerMessage{
    private String groupId;

    public MsgSetGroupId(String groupId){
        this.groupId = groupId;
    }

    public String getGroupId(){return groupId;}
}
