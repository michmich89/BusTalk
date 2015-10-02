package com.busgen.bustalk.model;

/**
 * Created by Johan on 2015-10-02.
 */
public class User implements IUser{

    private String nickname;
    private String[] interests;

    public User(String nickname, String[] interests){
        this.nickname = nickname;
        this.interests = interests;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    public String getNickname() {
        return nickname;
    }

    public String[] getInterests() {
        return interests;
    }

    public int getNbrOfInterests() {
        return interests.length;
    }
}
