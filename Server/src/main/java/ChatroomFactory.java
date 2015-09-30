/**
 * Created by Kristoffer on 2015-09-29.
 */


/**
 * This is a singleton, and unless another method for chatroom identification is implemented, this Needs to be a
 * singleton aswell, as the one instantiation of the class will take care of assigning a unique ID-number to a
 * chatroom.
 *
 * TODO: Hantera ID-nummer på ett hållbart sett (Hållbarare...)
 */
public class ChatroomFactory {

    private static ChatroomFactory chatroomFactory;
    private int idNbr;

    public static ChatroomFactory getFactory(){
        if(chatroomFactory == null){
            chatroomFactory = new ChatroomFactory();
        }
        return chatroomFactory;
    }

    private ChatroomFactory(){
        idNbr = 0;
    }

    public Chatroom createChatroom(String chatroomSubject){
        Chatroom temporaryReference = new Chatroom(this.idNbr, chatroomSubject);
        this.idNbr++;
        return temporaryReference;

    }

}
