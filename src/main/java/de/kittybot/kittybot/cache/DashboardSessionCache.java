package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.session.DashboardSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DashboardSessionCache{

	private static final Map<String, DashboardSession> SESSION_CACHE = new HashMap<>();
	private static final Map<String, Boolean> USER_SESSION_CACHE = new HashMap<>();

	private DashboardSessionCache(){
	}

	public static void addSession(final DashboardSession session){
		SESSION_CACHE.put(session.getUserId(), session);
		USER_SESSION_CACHE.put(session.getUserId(), true);
		Database.addSession(session);
	}

	public static void deleteSession(final String userId){
		Database.deleteSession(userId);
		USER_SESSION_CACHE.put(userId, false);
		GuildCache.uncacheUser(userId);
		SESSION_CACHE.remove(userId);
	}

	public static DashboardSession getSession(final String userId){
		var session = SESSION_CACHE.get(userId);
		if(session != null){
			return session;
		}
		session = Database.getSession(userId);
		if(session != null){
			SESSION_CACHE.put(userId, session);
		}
		return session;
	}

	public static boolean hasSession(final String userId){
		var hasSession = USER_SESSION_CACHE.get(userId);
		if(hasSession != null){
			return hasSession;
		}
		hasSession = Database.hasSession(userId);
		USER_SESSION_CACHE.put(userId, hasSession);
		return hasSession;
	}

}
