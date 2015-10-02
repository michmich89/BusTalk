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

    @Override
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public String[] getInterests() {
        return interests;
    }

    @Override
    public int getNbrOfInterests() {
        return interests.length;
    }
}
