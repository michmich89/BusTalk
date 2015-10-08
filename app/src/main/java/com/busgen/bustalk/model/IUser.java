package com.busgen.bustalk.model;

/**
 * Interface representing a Chat User with appropriate information.
 */
public interface IUser {

    void setUserName(String userName);
    void setInterest(String interest);
    String getUserName();
    String getInterest();
}
