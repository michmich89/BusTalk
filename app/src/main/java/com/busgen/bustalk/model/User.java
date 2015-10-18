package com.busgen.bustalk.model;

/**
 * Created by Johan on 2015-10-02.
 */
public class User implements IUser{

    private String userName;
    private String interest;

    public User(){}

    public User(String userName, String interest){
        this.userName = userName;
        this.interest = interest;
    }

    @Override
    public void setUserName(String userName) {
        System.out.println("Sätter namn till " + userName );
        this.userName = userName;
    }

    @Override
    public void setInterest(String interest) {
        this.interest = interest;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getInterest() {
        return interest;
    }

}
