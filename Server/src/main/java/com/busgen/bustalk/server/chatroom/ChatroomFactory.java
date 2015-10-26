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
 * TODO: Handle ID-number in a more efficient way, IE using a stack of old(removed) id-numbers to get IDs from first
 */
public class ChatroomFactory {

    private static ChatroomFactory chatroomFactory;
    private int normalChatIdNbr;
    private int mainChatIdNbr;

    private static class Holder {
        static final ChatroomFactory INSTANCE = new ChatroomFactory();
    }

    private ChatroomFactory(){
        normalChatIdNbr = Constants.NBR_OF_RESERVED_CHAT_IDS;
        mainChatIdNbr = 0;
    }

    public static ChatroomFactory getFactory(){
        return Holder.INSTANCE;
    }

    public Chatroom createChatroom(String chatroomSubject){
        Chatroom temporaryReference = new Chatroom(this.normalChatIdNbr, chatroomSubject);
        this.normalChatIdNbr++;
        return temporaryReference;
    }

    /**
     *
     * @param title the title of the room
     * @return the newly created main chatroom
     * @throws ChatIdNbrFullException if no ID-numbers is available
     */
    public Chatroom createMainChatroom(String title) throws ChatIdNbrFullException{

        if(mainChatIdNbr < Constants.NBR_OF_RESERVED_CHAT_IDS){
            Chatroom temporaryReference = new Chatroom(this.mainChatIdNbr, title);
            this.mainChatIdNbr++;
            return temporaryReference;
        }
        throw new ChatIdNbrFullException("All main chat id-number taken");
    }

}
