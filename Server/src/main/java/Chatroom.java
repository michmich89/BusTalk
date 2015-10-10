import javax.websocket.Session;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristoffer on 2015-09-29.
 */
public class Chatroom {

    private String chatroomTitle;
    private final int idNbr;
    private final List<User> chatroomUsers;

    /*
    TODO: (un)subscribe should not use Session as parameter, as that ties our server to this specific server solution
     */

    public Chatroom(int idNbr, String chatroomTitle){
        this.chatroomTitle = chatroomTitle;
        this.idNbr = idNbr;
        chatroomUsers = new ArrayList<User>();
    }

    /**
     * Method will add user to a chatroom, if user is not already subscribed to room
     *
     * @param user - the user you want to add
     * @return true if user was just added - false if user was already subscribed to room
     */
    public boolean subscribeToRoom(User user){
        if (!chatroomUsers.contains(user)) {
            return chatroomUsers.add(user);
        }
        return false;
    }

    /**
     * Method will remove a user from a chatroom if the user is subscribed to that room
     *
     * @param user - the user who wants to leave a room
     * @return true if user was able to be removed - false if the user wasn't subscribed to the room
     */
    public boolean unsubscribeToRoom(User user){
        return chatroomUsers.remove(user);
    }

    public int getIdNbr(){
        return this.idNbr;
    }

    public String getTitle () {
        return this.chatroomTitle;
    }

    public List<User> getChatroomUsers(){
        return new ArrayList<User>(chatroomUsers);
    }


}
