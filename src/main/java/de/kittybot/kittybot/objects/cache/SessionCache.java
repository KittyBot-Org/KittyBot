package de.kittybot.kittybot.objects.cache;

import com.jagrosh.jdautilities.oauth2.OAuth2Client;
import com.jagrosh.jdautilities.oauth2.Scope;
import com.jagrosh.jdautilities.oauth2.exceptions.InvalidStateException;
import com.jagrosh.jdautilities.oauth2.session.Session;
import de.kittybot.kittybot.database.Database;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SessionCache {
    private static final Map<String, Session> SESSION_CACHE = new HashMap<>();         // Map<UserId, Session>
    private static final Map<String, String> ACTIVE_SESSIONS_CACHE = new HashMap<>();  // Map<SessionId, UserId>

    private SessionCache(){}

    public static Session createSession(final OAuth2Client oAuth2Client, final String code, final String state, final String key, final Scope[] scopes) throws InvalidStateException, IOException{
        final var newSession = oAuth2Client.startSession(code, state, key, scopes).complete();
        final var userId = oAuth2Client.getUser(newSession).complete().getId();
        SESSION_CACHE.put(userId, newSession);
        ACTIVE_SESSIONS_CACHE.put(key, userId);
        Database.addSession(userId, key);
        return newSession;
    }

    public static Session getSession(final String userId){
        return SESSION_CACHE.get(userId);
    }

    public static boolean sessionExists(final String sessionId){
        return ACTIVE_SESSIONS_CACHE.containsKey(sessionId) || Database.sessionExists(sessionId);
    }

    public static String getUserId(final String sessionId){
        final var cached = ACTIVE_SESSIONS_CACHE.get(sessionId);
        return cached != null ? cached : Database.getSessionUserId(sessionId);
    }
}