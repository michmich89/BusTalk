package com.busgen.bustalk.model;

import junit.framework.TestCase;

/**
 * Created by nalex on 30/10/2015.
 */
public class UserTest extends TestCase {
    protected User user;

    @Override
    public void setUp(){
        user = new User();
        user.setUserName("test");
    }

    public void testEqualsWithSelf(){
        assertTrue(user.equals(user));
    }

    public void testEqualsWithNull(){
        assertFalse(user.equals(null));
    }

    public void testEqualsIfEqual(){
        User otherUser = new User();
        otherUser.setUserName("test");
        assertTrue(user.equals(otherUser));
    }

    public void testEqualsIfEqualIfNotEqual(){
        User otherUser = new User();
        otherUser.setUserName("notTest");
        assertFalse(user.equals(otherUser));
    }

    public void testHascodeIfEqual(){
        User otherUser = new User();
        otherUser.setUserName("test");
        assertTrue(user.hashCode() == otherUser.hashCode());
    }

    public void testHascodeIfNotEqual(){
        User otherUser = new User();
        otherUser.setUserName("notTest");
        assertFalse(user.hashCode() == otherUser.hashCode());
    }
}
