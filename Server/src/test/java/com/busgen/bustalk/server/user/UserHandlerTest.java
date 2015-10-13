package com.busgen.bustalk.server.user;

import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import org.junit.Test;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by danie on 2015-10-13.
 */
public class UserHandlerTest {
    private ChatroomHandler chatroomHandler;
    private UserHandler userHandler;
    private User user;

    public UserHandlerTest () {
        chatroomHandler = ChatroomHandler.getInstance();
        userHandler = UserHandler.getInstance();

        //user = new User("username", "interests");
        //user.setGroupId("testGroup");
    }

    @Test
    public void testIfUserIsCreatedWhenUserDoesNotExistAndWantsToSetNameAndInterests() {
        String name = "new user";
        String interests = "interests";
        // Should create a new user as "new user" is an allowed name
        Session session = createNewSession();
        userHandler.setUserNameAndInterests(null, session, name, interests);

        assertTrue(userHandler.getUser(session).getName().equals(name));
    }

    @Test
    public void testIfUserCannotChooseANameThatIsAlreadyTaken() {
        Session firstUser = createNewSession();
        userHandler.setUserNameAndInterests(null, firstUser, "carl", "nothing");
        // Another user wants to have the same name as user above
        Session secondUser = createNewSession();
        userHandler.setUserNameAndInterests(null, secondUser, "carl", "everything");

        assertTrue(userHandler.getUser(firstUser) != null && userHandler.getUser(secondUser) == null);
    }

    @Test
    public void testIfExistingUserCanChangeNameToTheSameName() {
        Session userSession = createNewSession();
        userHandler.setUserNameAndInterests(null, userSession, "iamnew", "unit testing");
        User user = userHandler.getUser(userSession);
        userHandler.setUserNameAndInterests(user, userSession, "IamNew", "something else");

        assertTrue(user.getName().equals("IamNew"));
    }


    private Session createNewSession() {
        return new Session() {
            @Override
            public WebSocketContainer getContainer() {
                return null;
            }

            @Override
            public void addMessageHandler(MessageHandler handler) throws IllegalStateException {

            }

            @Override
            public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) {

            }

            @Override
            public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) {

            }

            @Override
            public Set<MessageHandler> getMessageHandlers() {
                return null;
            }

            @Override
            public void removeMessageHandler(MessageHandler handler) {

            }

            @Override
            public String getProtocolVersion() {
                return null;
            }

            @Override
            public String getNegotiatedSubprotocol() {
                return null;
            }

            @Override
            public List<Extension> getNegotiatedExtensions() {
                return null;
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public boolean isOpen() {
                return false;
            }

            @Override
            public long getMaxIdleTimeout() {
                return 0;
            }

            @Override
            public void setMaxIdleTimeout(long milliseconds) {

            }

            @Override
            public void setMaxBinaryMessageBufferSize(int length) {

            }

            @Override
            public int getMaxBinaryMessageBufferSize() {
                return 0;
            }

            @Override
            public void setMaxTextMessageBufferSize(int length) {

            }

            @Override
            public int getMaxTextMessageBufferSize() {
                return 0;
            }

            @Override
            public RemoteEndpoint.Async getAsyncRemote() {
                return null;
            }

            @Override
            public RemoteEndpoint.Basic getBasicRemote() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public void close() throws IOException {

            }

            @Override
            public void close(CloseReason closeReason) throws IOException {

            }

            @Override
            public URI getRequestURI() {
                return null;
            }

            @Override
            public Map<String, List<String>> getRequestParameterMap() {
                return null;
            }

            @Override
            public String getQueryString() {
                return null;
            }

            @Override
            public Map<String, String> getPathParameters() {
                return null;
            }

            @Override
            public Map<String, Object> getUserProperties() {
                return null;
            }

            @Override
            public Principal getUserPrincipal() {
                return null;
            }

            @Override
            public Set<Session> getOpenSessions() {
                return null;
            }
        };
    }
}