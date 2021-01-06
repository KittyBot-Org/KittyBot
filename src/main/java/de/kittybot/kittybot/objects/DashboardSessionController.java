package de.kittybot.kittybot.objects;

import com.jagrosh.jdautilities.oauth2.session.SessionController;
import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.modules.DashboardSessionModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DashboardSessionController implements SessionController<DashboardSession>{

	private static final Logger LOG = LoggerFactory.getLogger(DashboardSessionController.class);

	private final DashboardSessionModule dashboardSessionModule;

	public DashboardSessionController(DashboardSessionModule dashboardSessionModule){
		this.dashboardSessionModule = dashboardSessionModule;
	}

	@Override
	public DashboardSession getSession(String userId){
		return this.dashboardSessionModule.get(Long.parseLong(userId));
	}

	@Override
	public DashboardSession createSession(final SessionData sessionData){
		var session = new DashboardSession(sessionData);
		try{
			session.setUserId(this.dashboardSessionModule.getOAuth2Client().getUser(session).complete().getIdLong());
			this.dashboardSessionModule.add(session);
			return session;
		}
		catch(IOException e){
			LOG.error("Error while creating session", e);
		}
		return null;
	}

}
