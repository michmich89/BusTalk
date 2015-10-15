package com.busgen.bustalk.server;

import com.busgen.bustalk.server.chatroom.ChatroomHandler;
import com.busgen.bustalk.server.message.MessageType;
import com.busgen.bustalk.server.message.UserMessage;
import com.busgen.bustalk.server.user.User;
import com.busgen.bustalk.server.user.UserHandler;
import org.json.JSONObject;
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
 * Created by danie on 2015-10-15.
 */
public class BusTalkHandlerTest {

    private ChatroomHandler chatroomHandler;
    private UserHandler userHandler;
    private BusTalkHandler busTalkHandler;
    private User user;

    public BusTalkHandlerTest(){
        chatroomHandler = ChatroomHandler.getInstance();
        userHandler = UserHandler.getInstance();
        busTalkHandler = BusTalkHandler.getInstance();
    }

    @Test
    public void TestChangeGroupIdOfUser(){
        //Create objects needed
        User user = new User("username", "userinterests");
        Session session = createNewSession();
        userHandler.addUser(user, session);

        //Create first simulated userMessage
        JSONObject message = new JSONObject();
        message.put("type", MessageType.CHANGE_GROUP_ID);
        message.put("groupId", "firstGroup");
        UserMessage userMessage = new UserMessage(message);

        //Send first message
        busTalkHandler.handleInput(userMessage, session);

        //Save ID
        String firstGroupId = user.getGroupId();
        System.out.println(firstGroupId);

        //Create second simulated userMessage
        JSONObject messageTwo = new JSONObject();
        messageTwo.put("type", MessageType.CHANGE_GROUP_ID);
        messageTwo.put("groupId", "secondGroup");
        UserMessage userMessageTwo = new UserMessage(messageTwo);

        //Send second message
        busTalkHandler.handleInput(userMessageTwo, session);

        //Save second ID
        String secondGroupId = user.getGroupId();
        System.out.println(secondGroupId);

        assertTrue(firstGroupId != null && secondGroupId != null && !firstGroupId.equals(secondGroupId));

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