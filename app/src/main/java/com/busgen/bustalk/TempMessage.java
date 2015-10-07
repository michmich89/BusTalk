package com.busgen.bustalk;

/**
 * Created by miche on 2015-10-02.
 */
public class TempMessage {
    private int id;
    private boolean isMe;
    private String message;
    private int userId;
    private String date;
    private String userName;

    public TempMessage() {
    }

    public TempMessage(boolean isMe, String message, String date, String userName) {
        this.isMe = isMe;
        this.message = message;
        this.date = date;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getIsMe() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
