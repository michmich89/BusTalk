package com.busgen.bustalk.server.user;

/**
 * Interface for a user.
 *
 * Created by Kristoffer on 2015-10-12.
 */
public interface IUser {

    public String getName();
    public void setName(String newName);
    public String getInterests();
    public void setInterests(String newInterests);
}
