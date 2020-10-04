package de.kittybot.kittybot.objects.session;

import com.jagrosh.jdautilities.oauth2.session.SessionController;
import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.WebService;
import de.kittybot.kittybot.cache.DashboardSessionCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DashboardSessionController implements SessionController<DashboardSession>{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionController.class);

	public DashboardSessionController(){
	}

	@Override
	public DashboardSession getSession(final String identifier){
		return DashboardSessionCache.getSession(identifier);
	}

	@Override
	public DashboardSession createSession(final SessionData sessionData){
		var session = new DashboardSession(null, sessionData);
		try{
			session.setUserId(WebService.getOAuth2Client().getUser(session).complete().getId());
			DashboardSessionCache.addSession(session);
			return session;
		}
		catch(IOException e){
			LOG.error("Error while creating session", e);
		}
		return null;
	}


}
