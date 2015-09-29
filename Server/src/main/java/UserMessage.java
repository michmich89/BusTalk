/**
 * Created by Kristoffer on 2015-09-29.
 */
public class UserMessage {

    private final String type, date, message;
    private final int chatID;

    public UserMessage(String type, String date, String message, int chatID){
        this.type = type;
        this.date = date;
        this.message = message;
        this.chatID = chatID;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public int getChatID() {
        return chatID;
    }

}
