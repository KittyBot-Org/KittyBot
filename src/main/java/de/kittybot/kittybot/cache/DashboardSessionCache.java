package de.kittybot.kittybot.cache;

import de.kittybot.kittybot.database.Database;
import de.kittybot.kittybot.objects.session.DashboardSession;

import java.util.HashMap;
import java.util.Map;

public class DashboardSessionCache{

	private static final Map<String, DashboardSession> SESSION_CACHE = new HashMap<>();

	private DashboardSessionCache(){}

	public static void addSession(final DashboardSession session){
		SESSION_CACHE.put(session.getUserId(), session);
		Database.addSession(session);
	}

	public static void deleteSession(final String userId){
		var session = SESSION_CACHE.get(userId);
		if(session == null){
			return;
		}
		Database.deleteSession(userId);
		SESSION_CACHE.remove(userId);
		GuildCache.uncacheUser(userId);
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
		return SESSION_CACHE.containsKey(userId) || Database.hasSession(userId);
	}

}