package com.busgen.bustalk;

import com.busgen.bustalk.events.Event;
import com.busgen.bustalk.events.ToServerEvent;
import com.busgen.bustalk.model.IEventBusListener;
import com.busgen.bustalk.model.IServerMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChatMessage;
import com.busgen.bustalk.model.ServerMessages.MsgChooseNickname;
import com.busgen.bustalk.model.ServerMessages.MsgCreateRoom;
import com.busgen.bustalk.model.ServerMessages.MsgJoinRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLeaveRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgLostUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNewChatRoom;
import com.busgen.bustalk.model.ServerMessages.MsgNewUserInChat;
import com.busgen.bustalk.model.ServerMessages.MsgNicknameAvailable;

/**
 * Created by Alexander Kloutschek on 2015-10-02.
 */
public class ServerCommunicator implements IEventBusListener {

    @Override
    public void onEvent(Event event) {

        IServerMessage message = event.getMessage();

        if (event instanceof ToServerEvent) {
            if (message instanceof MsgChatMessage) {

            } else if (message instanceof MsgChooseNickname) {

            } else if (message instanceof MsgChooseNickname) {

            } else if (message instanceof MsgCreateRoom) {

            } else if (message instanceof MsgJoinRoom) {

            } else if (message instanceof MsgLeaveRoom) {

            } else if (message instanceof MsgLostChatRoom) {

            } else if (message instanceof MsgLostUserInChat) {

            } else if (message instanceof MsgNewChatRoom) {

            } else if (message instanceof MsgNewUserInChat) {

            } else if (message instanceof MsgNicknameAvailable) {
            }
        }
    }
}
