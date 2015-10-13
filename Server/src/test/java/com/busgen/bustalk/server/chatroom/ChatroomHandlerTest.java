package com.busgen.bustalk.server.chatroom;

import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.junit.Before;
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
public class ChatroomHandlerTest {

    private Session session;
    private ChatroomHandler chatroomHandler;
    private UserHandler userHandler;
    private User user;

    public ChatroomHandlerTest () {
        chatroomHandler = ChatroomHandler.getInstance();
        userHandler = UserHandler.getInstance();

        user = new User("username", "interests");
        user.setGroupId("testGroup");
        session = new Session() {
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

        userHandler.addUser(user, session);

        chatroomHandler.createChatroom(user, "chatroom 1"); // This will have chat id 100
    }

    @Test
    public void testIfUserCreatedChatroomsAreCreatedCorrectly() {
        Chatroom chatroom = chatroomHandler.createChatroom(user, "test chatroom");

        assertTrue(chatroomHandler.getListOfOpenChatrooms().contains(chatroom)
                && chatroomHandler.getGroupOfChatrooms(user.getGroupId()).contains(chatroom)
                && chatroom.getChatroomUsers().contains(user));
    }

    @Test
    public void testIfUserCreatedChatroomIsDeletedWhenLastUserLeaves() {
        Chatroom chatroom = chatroomHandler.getChatroom(100);
        chatroomHandler.leaveChatroom(user, chatroom);

        assertFalse(chatroomHandler.getListOfOpenChatrooms().contains(chatroom));
    }

    @Test
    public void testIfUserCreatedChatroomsHaveTheCorrectGroupId() {
        Chatroom chatroom = chatroomHandler.createChatroom(user, "test");

        assertTrue(chatroomHandler.getGroupOfChatrooms(user.getGroupId()).contains(chatroom));
    }

    @Test
    public void testIfServerCreatedChatroomsAreNotDeletedWhenLastUserLeaves() {
        int chatId = 0;
        chatroomHandler.createChatroom("server chat", chatId, "testGroup");
        Chatroom newChatroom = chatroomHandler.getChatroom(chatId);
        chatroomHandler.joinChatroom(user, newChatroom);
        chatroomHandler.leaveChatroom(user, newChatroom);

        assertTrue(chatroomHandler.getListOfOpenChatrooms().contains(newChatroom));
    }
}