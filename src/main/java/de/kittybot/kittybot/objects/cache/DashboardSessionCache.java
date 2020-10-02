package de.kittybot.kittybot.objects.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.session.DashboardSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardSessionCache{

	private static final Map<String, DashboardSession> SESSION_CACHE = new HashMap<>();
	private static final List<String> USER_SESSION_CACHE = new ArrayList<>();

	private DashboardSessionCache(){}

	public static void addSession(final DashboardSession session){
		SESSION_CACHE.put(session.getSessionKey(), session);
		USER_SESSION_CACHE.add(session.getUserId());
		Database.addSession(session);
	}

	public static DashboardSession getSession(final String sessionKey){
		var session = SESSION_CACHE.get(sessionKey);
		if(session != null){
			return session;
		}
		session = Database.getSession(sessionKey);
		if(session != null){
			SESSION_CACHE.put(sessionKey, session);
		}
		return session;

	}

	public static void deleteSession(final String sessionKey){
		GuildCache.uncacheUser(SESSION_CACHE.get(sessionKey).getUserId()); // TODO we should only uncache if there's only 1 session of the user
		SESSION_CACHE.remove(sessionKey);
		Database.deleteSession(sessionKey);
	}

	public static boolean sessionExists(final String sessionKey){
		return SESSION_CACHE.containsKey(sessionKey) || Database.sessionExists(sessionKey);
	}

	public static boolean hasSession(final String userId){
		return USER_SESSION_CACHE.contains(userId) || Database.hasSession(userId);
	}

}