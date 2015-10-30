package com.busgen.bustalk.model;

import java.io.Serializable;

/**
 * Interface representing a Chat User with appropriate information.
 */
public interface IUser extends Serializable{

    void setUserName(String userName);
    void setInterest(String interest);
    String getUserName();
    String getInterest();
}
