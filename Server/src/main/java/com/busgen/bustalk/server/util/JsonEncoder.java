package com.busgen.bustalk.server.util;

import com.busgen.bustalk.server.message.UserMessage;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

/**
 * Encodes messages sent to the client from a UserMessage to a string
 *
 * Created by Kristoffer on 2015-09-30.
 */
public class JsonEncoder implements Encoder.Text<UserMessage> {


    @Override
    public String encode(UserMessage userMessage) throws EncodeException {


        return userMessage.toString();
    }

    @Override
    public void init(EndpointConfig config) {

    }

    @Override
    public void destroy() {

    }
}
