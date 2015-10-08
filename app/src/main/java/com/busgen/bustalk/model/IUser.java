package com.busgen.bustalk.model;

/**
 * Interface representing a Chat User with appropriate information.
 */
public interface IUser {

    public void setUserName(String userName);
    public void setInterest(String interest);
    public String getUserName();
    public String getInterest();
}
