package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.BotList;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.types.YearToSecond;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.VOTERS;

public class VoteModule extends Module{

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		if(event.getGuild().getIdLong() == Config.SUPPORT_GUILD_ID){
			this.modules.scheduleAtFixedRate(this::checkVoters, 0, 30, TimeUnit.MINUTES);
		}
	}

	private void checkVoters(){
		var guild = this.modules.getGuildById(Config.SUPPORT_GUILD_ID);
		if(guild == null){
			return;
		}
		var role = guild.getRoleById(Config.VOTER_ROLE_ID);
		if(role == null){
			return;
		}
		var result = this.modules.get(DatabaseModule.class).getCtx().deleteFrom(VOTERS).where(VOTERS.VOTE_EXPIRY.lessOrEqual(LocalDateTime.now())).returning().fetch();
		for(var r : result){
			guild.removeRoleFromMember(r.getUserId(), role).reason("vote expired").queue();
		}
	}

	public void addVote(long userId, BotList botList, int voteMultiplier){
		var voteDuration = Duration.of(botList.getVoteCooldown(), botList.getTimeUnit()).multipliedBy(voteMultiplier);

		this.modules.get(DatabaseModule.class).getCtx().insertInto(VOTERS)
			.columns(VOTERS.USER_ID)
			.values(userId)
			.onConflict(VOTERS.USER_ID)
			.doUpdate()
			.set(VOTERS.VOTE_EXPIRY, VOTERS.VOTE_EXPIRY.add(YearToSecond.valueOf(voteDuration)))
			.execute();

		var jda = this.modules.getJDA();
		jda.retrieveUserById(userId).queue(user -> this.modules.get(EventLogModule.class).send(jda, "Vote", "´" + user.getAsTag() + "`(`" + user.getId() + "`) voted on " + MessageUtils.maskLink("`" + botList.getName() + "`", botList.getUrl())));

		var guild = this.modules.getGuildById(Config.SUPPORT_GUILD_ID);
		if(guild == null){
			return;
		}
		var role = guild.getRoleById(Config.VOTER_ROLE_ID);
		if(role == null){
			return;
		}
		guild.addRoleToMember(userId, role).reason("voted on " + botList.getName()).queue();
	}

}
