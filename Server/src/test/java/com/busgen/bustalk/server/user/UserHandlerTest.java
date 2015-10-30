package com.busgen.bustalk.server.user;

import org.junit.Test;
import org.mockito.Mockito;

import javax.websocket.Session;

import static org.junit.Assert.assertTrue;

/**
 * Created by danie on 2015-10-13.
 */
public class UserHandlerTest {
    private UserHandler userHandler;

    public UserHandlerTest () {
        userHandler = UserHandler.getInstance();
    }

    @Test
    public void testIfUserIsCreatedWhenUserDoesNotExistAndWantsToSetNameAndInterests() {
        String name = "new user";
        String interests = "interests";
        // Should create a new user as "new user" is an allowed name
        Session session = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, session, name, interests);

        assertTrue(userHandler.getUser(session).getName().equals(name));
    }

    @Test
    public void testIfUserCannotChooseANameThatIsAlreadyTaken() {
        Session firstUser = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, firstUser, "carl", "nothing");
        // Another user wants to have the same name as user above
        Session secondUser = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, secondUser, "carl", "everything");

        assertTrue(userHandler.getUser(firstUser) != null && userHandler.getUser(secondUser) == null);
    }

    @Test
    public void testIfExistingUserCanChangeNameToTheSameName() {
        String name = "NewName";

        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "newname", "unit testing");
        IUser user = userHandler.getUser(userSession);
        userHandler.setUserNameAndInterests(user, userSession, name, "something else");

        assertTrue(user.getName().equals(name));
    }

    @Test
    public void testIfUserIsRemovedCorrectly() {
        Session userSession = Mockito.mock(Session.class);
        userHandler.setUserNameAndInterests(null, userSession, "user name", "some interests");
        IUser user = userHandler.getUser(userSession);

        userHandler.removeUser(user);
        assertTrue(!userHandler.getUsers().contains(user) && userHandler.isNameAllowed(user.getName()));
    }
}