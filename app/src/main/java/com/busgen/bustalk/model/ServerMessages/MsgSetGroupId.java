package com.busgen.bustalk.model.ServerMessages;

/**
 * Created by nalex on 16/10/2015.
 */
public class MsgSetGroupId {
    private String groupId;

    MsgSetGroupId(String groupId){
        this.groupId = groupId;
    }

    public String getGroupId(){return groupId;}
}
