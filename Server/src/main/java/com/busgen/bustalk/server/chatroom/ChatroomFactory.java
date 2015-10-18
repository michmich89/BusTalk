package com.busgen.bustalk.server.chatroom; /**
 * Created by Kristoffer on 2015-09-29.
 */


import com.busgen.bustalk.server.util.Constants;

/**
 * Creates chat rooms on request.
 *
 * This is a singleton, and Needs to be a singleton aswell, as the one instantiation of the class will take care of
 * assigning a unique ID-number to a chatroom.
 *
 * The first 100 id numbers are reserved for rooms that should never be removed, for example a main room for a bus
 *
 * TODO: Hantera ID-nummer på ett hållbart sett (Hållbarare...)
 */
public class ChatroomFactory {

    private static ChatroomFactory chatroomFactory;
    private int idNbr;

    private static class Holder {
        static final ChatroomFactory INSTANCE = new ChatroomFactory();
    }

    private ChatroomFactory(){
        idNbr = Constants.NBR_OF_RESERVED_CHAT_IDS;
    }

    public static ChatroomFactory getFactory(){
        return Holder.INSTANCE;
    }

    public Chatroom createChatroom(String chatroomSubject){
        Chatroom temporaryReference = new Chatroom(this.idNbr, chatroomSubject);
        this.idNbr++;
        return temporaryReference;
    }

    public Chatroom createChatroom(String name, int chatId) {
        if (chatId < Constants.NBR_OF_RESERVED_CHAT_IDS - 1) {
            return new Chatroom(chatId, name);
        }
        return null;
    }

}
