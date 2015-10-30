package com.busgen.bustalk.server.user;

/**
 * Holds all important data related to a user.
 *
 * Created by Kristoffer on 2015-09-29.
 */
public class User implements IUser {

    private String name;
    private String interests;

    public User(String name, String interests){
        this.name = name;
        this.interests = interests;
    }

    @Override
    public boolean equals(Object o){
        if(o == null) return false;
        if(o == this) return true;
        if(!(o instanceof User)) return false;

        User user = (User)o;

        return (user.name.equalsIgnoreCase(this.name));
    }


    @Override
    public int hashCode(){
        return name.toLowerCase().hashCode()*17*5*23;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String newName){
        this.name = newName;
    }

    public String getInterests(){
        return this.interests;
    }

    public void setInterests(String newInterests){
        this.interests = newInterests;
    }

}
