package de.kittybot.kittybot.objects.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.session.DashboardSession;

import java.util.HashMap;
import java.util.Map;

public class DashboardSessionCache{

	private static final Map<String, DashboardSession> SESSION_CACHE = new HashMap<>();

	private DashboardSessionCache(){}

	public static void addSession(final DashboardSession session){
		SESSION_CACHE.put(session.getSessionKey(), session);
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
		SESSION_CACHE.remove(sessionKey);
		Database.deleteSession(sessionKey);
	}

	public static boolean sessionExists(final String sessionKey){
		return SESSION_CACHE.containsKey(sessionKey) || Database.sessionExists(sessionKey);
	}

}