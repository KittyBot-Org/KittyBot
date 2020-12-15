package de.kittybot.kittybot.objects;

import com.jagrosh.jdautilities.oauth2.session.SessionController;
import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.managers.DashboardSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DashboardSessionController implements SessionController<DashboardSession>{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionController.class);

	private final DashboardSessionManager dashboardSessionManager;

	public DashboardSessionController(DashboardSessionManager dashboardSessionManager){
		this.dashboardSessionManager = dashboardSessionManager;
	}

	@Override
	public DashboardSession getSession(String userId){
		return this.dashboardSessionManager.get(Long.parseLong(userId));
	}

	@Override
	public DashboardSession createSession(final SessionData sessionData){
		var session = new DashboardSession(sessionData);
		try{
			session.setUserId(this.dashboardSessionManager.getOAuth2Client().getUser(session).complete().getIdLong());
			this.dashboardSessionManager.add(session);
			return session;
		}
		catch(IOException e){
			LOG.error("Error while creating session", e);
		}
		return null;
	}


}
