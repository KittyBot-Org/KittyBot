package de.kittybot.kittybot.objects.session;

import com.jagrosh.jdautilities.oauth2.session.SessionController;
import com.jagrosh.jdautilities.oauth2.session.SessionData;
import de.kittybot.kittybot.modules.DashboardSessionModule;

import java.io.IOException;

public class DashboardSessionController implements SessionController<DashboardSession>{

	private final DashboardSessionModule dashboardSessionModule;

	public DashboardSessionController(DashboardSessionModule dashboardSessionModule){
		this.dashboardSessionModule = dashboardSessionModule;
	}

	@Override
	public DashboardSession getSession(String userId){
		return this.dashboardSessionModule.get(Long.parseLong(userId));
	}

	@Override
	public DashboardSession createSession(SessionData sessionData){
		var session = new DashboardSession(sessionData);
		try{
			var user = this.dashboardSessionModule.getOAuth2Client().getUser(session).complete();
			session.setUserId(user.getIdLong());
			this.dashboardSessionModule.add(session);
			return session;
		}
		catch(IOException ignored){}
		return null;
	}

}
