package de.kittybot.kittybot.modules;

import de.kittybot.kittybot.objects.enums.BotList;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import org.jetbrains.annotations.NotNull;
import org.jooq.types.YearToSecond;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static de.kittybot.kittybot.jooq.Tables.VOTERS;

public class VoteModule extends Module{

	private static final Logger LOG = LoggerFactory.getLogger(VoteModule.class);

	@Override
	public void onGuildReady(@NotNull GuildReadyEvent event){
		if(event.getGuild().getIdLong() != Config.SUPPORT_GUILD_ID){
			return;
		}
		this.modules.scheduleAtFixedRate(this::checkVoters, 0, 30, TimeUnit.MINUTES);
	}

	@Override
	public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event){
		var guild = event.getGuild();
		if(guild.getIdLong() != Config.SUPPORT_GUILD_ID){
			return;
		}
		try(var ctx = this.modules.get(DatabaseModule.class).getCtx().selectFrom(VOTERS)){
			var res = ctx.where(VOTERS.USER_ID.eq(event.getUser().getIdLong())).fetchOne();
			if(res == null){
				return;
			}
			var role = guild.getRoleById(Config.VOTER_ROLE_ID);
			if(role == null){
				return;
			}
			guild.addRoleToMember(res.getUserId(), role).queue();
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
		var voteDuration = Duration.of((long) (botList.getVoteCooldown() * 1.5), botList.getTimeUnit()).multipliedBy(voteMultiplier);

		this.modules.get(DatabaseModule.class).getCtx().insertInto(VOTERS)
			.columns(VOTERS.USER_ID, VOTERS.VOTE_EXPIRY)
			.values(userId, LocalDateTime.now().plus(voteDuration))
			.onConflict(VOTERS.USER_ID)
			.doUpdate()
			.set(VOTERS.VOTE_EXPIRY, VOTERS.VOTE_EXPIRY.add(YearToSecond.valueOf(voteDuration)))
			.execute();

		var jda = this.modules.getJDA();
		jda.retrieveUserById(userId).queue(user -> this.modules.get(EventLogModule.class).send(jda, "Vote", "`" + user.getAsTag() + "`(`" + user.getId() + "`) voted on " + MessageUtils.maskLink(botList.getName(), botList.getUrl())));

		var guild = this.modules.getGuildById(Config.SUPPORT_GUILD_ID);
		if(guild == null){
			return;
		}
		var role = guild.getRoleById(Config.VOTER_ROLE_ID);
		if(role == null){
			return;
		}
		if(!guild.getSelfMember().canInteract(role)){
			LOG.error("I can't interact with the provided voter role: {}", role.getId());
			return;
		}
		guild.addRoleToMember(userId, role).reason("voted on " + botList.getName()).queue();
	}

}
