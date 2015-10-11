package com.busgen.bustalk.server.user;

import com.busgen.bustalk.server.util.Constants;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import javax.websocket.Session;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserHandler {
    private final BiMap<User, Session> userToSession;
    private final List<String> disallowedNames;
    private static final Logger LOGGER = Logger.getLogger(UserHandler.class.getName());

    private static class Holder {
        static final UserHandler INSTANCE = new UserHandler();
    }

    private UserHandler() {
        this.userToSession = Maps.synchronizedBiMap(HashBiMap.<User, Session>create());
        this.disallowedNames = Collections.synchronizedList(new ArrayList<String>());

        disallowedNames.add("Alexander Kloutschek");
    }

    public static UserHandler getInstance() {
        return Holder.INSTANCE;
    }

    public User getUser(Session session) {
        return userToSession.inverse().get(session);
    }

    public Session getSession(User user) {
        return userToSession.get(user);
    }

    public List<User> getUsers() {
        return new ArrayList<User>(userToSession.keySet());
    }

    public List<Session> getSessions() {
        return new ArrayList<Session>(userToSession.values());
    }

    public boolean isNameAllowed(String name) {
        return !disallowedNames.contains(name.toLowerCase());
    }

    private void addDisallowedName(String name) {
        name = name.toLowerCase();
        if (!disallowedNames.contains(name)) {
            disallowedNames.add(name);
            LOGGER.log(Level.INFO, String.format("Added \"{0}\" to list of disallowed names"), name);
        }
    }

    private void removeDisallowedName(String name) {
        name = name.toLowerCase();
        if (disallowedNames.remove(name)) {
            LOGGER.log(Level.INFO, String.format("Removed \"{0}\" from list of disallowed names"), name);
        }
    }

    public void addUser(User user, Session session) {
        userToSession.put(user, session);
        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Added to user list"), new Object[]{session.getId(), user.getName()});
        addDisallowedName(user.getName());
        LOGGER.log(Level.INFO, String.format("\"{0}\" was added to the list of disallowed names"), user.getName());
    }

    public void removeUser(User user) {
        removeDisallowedName(user.getName());
        LOGGER.log(Level.INFO, String.format("\"{0}\" was removed from the list of disallowed names"), user.getName());
        userToSession.remove(user);
        LOGGER.log(Level.INFO, String.format("[{0}:{1}] Removed from user list"), new Object[]{userToSession.get(user).getId(), user.getName()});
    }

    public boolean setUserNameAndInterests(User user, Session session, String name, String interests) {
        if (user != null && (isNameAllowed(name) || user.getName().equals(name))) {
            String oldName = user.getName();
            String oldInterests = user.getInterests();

            user.setName(name);
            user.setInterests(interests);

            //addUser(user, session);

            LOGGER.log(Level.INFO, String.format("[{0}:{1}] Name changed to \"{2}\" and interest changed from \"{3}\" to \"{4}\""),
                    new Object[]{session.getId(), oldName, user.getName(), oldInterests, user.getInterests()});
        } else if (isNameAllowed(name)) {
            user = new User(name, interests);
            addUser(user, session);

            LOGGER.log(Level.INFO, String.format("[{0}:{1}] Created with interests \"{2}\""),
                    new Object[]{session.getId(), user.getName(), user.getInterests()});
        } else {
            return false;
        }

        return true;
    }
}
