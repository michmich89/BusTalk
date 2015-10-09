/**
 * Created by Kristoffer on 2015-09-29.
 */


/**
 * This is a singleton, and unless another method for chatroom identification is implemented, this Needs to be a
 * singleton aswell, as the one instantiation of the class will take care of assigning a unique ID-number to a
 * chatroom.
 *
 * The first 100 id numbers are reserved for rooms that should never be removed, for example a main room for a bus
 *
 * TODO: Hantera ID-nummer på ett hållbart sett (Hållbarare...)
 */
public class ChatroomFactory {

    private static ChatroomFactory chatroomFactory;
    private int idNbr;

    private static class Holder {
        static final ChatroomFactory INSTACE = new ChatroomFactory();
    }

    public static ChatroomFactory getFactory(){
        return Holder.INSTACE;
    }

    private ChatroomFactory(){
        idNbr = 101;
    }

    public Chatroom createChatroom(String chatroomSubject){
        Chatroom temporaryReference = new Chatroom(this.idNbr, chatroomSubject);
        this.idNbr++;
        return temporaryReference;

    }

}
