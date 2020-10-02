package de.kittybot.kittybot.objects.session;

import de.kittybot.kittybot.objects.cache.DashboardSessionCache;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.jetbrains.annotations.NotNull;

public class SessionUserCachePolicy implements MemberCachePolicy{

	@Override
	public boolean cacheMember(@NotNull final Member member){
		return DashboardSessionCache.hasSession(member.getId());
	}

}