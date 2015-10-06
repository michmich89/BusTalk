import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristoffer on 2015-09-29.
 */
public class Chatroom {

    private String chatroomTitle;
    private final int idNbr;
    private final List<Session> chatroomUsers;

    /*
    TODO: (un)subscribe should not use Session as parameter, as that ties our server to this specific server solution
     */

    public Chatroom(int idNbr, String chatroomTitle){
        this.chatroomTitle = chatroomTitle;
        this.idNbr = idNbr;
        chatroomUsers = new ArrayList<Session>();
    }

    public void subscribeToRoom(Session userSession){
        chatroomUsers.add(userSession);
    }

    public void unsubscribeToRoom(Session userSession){
        chatroomUsers.remove(userSession);
    }

    public int getIdNbr(){
        return this.idNbr;
    }

    public String getTitle () {
        return this.chatroomTitle;
    }

    public List<Session> getChatroomUsers(){
        return new ArrayList<Session>(chatroomUsers);
    }


}
