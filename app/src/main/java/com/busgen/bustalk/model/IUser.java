package com.busgen.bustalk.model;

/**
 * Interface representing a Chat User with appropriate information.
 */
public interface IUser {

    public void setNickname(String nickname);
    public void setInterests(String interests);
    public String getNickname();
    public String getInterests();
}
