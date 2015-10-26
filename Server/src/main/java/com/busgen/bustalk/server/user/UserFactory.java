package com.busgen.bustalk.server.user;

public class UserFactory {

    public UserFactory(){

    }

    public User createUser(String name, String interests){
        return new User(name, interests);
    }
}
