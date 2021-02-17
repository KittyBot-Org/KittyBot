package de.kittybot.kittybot.slashcommands.interaction;

import de.kittybot.kittybot.objects.module.Modules;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

public class Interaction{

	private final long id, channelId;
	private final InteractionType type;
	private final InteractionData data;
	private final Guild guild;
	private final Member member;
	private final String token;
	private final int version;
	private final Modules modules;
	private final JDA jda;

	protected Interaction(long id, InteractionType type, InteractionData data, Guild guild, long channelId, Member member, String token, int version, Modules modules, JDA jda){
		this.id = id;
		this.type = type;
		this.data = data;
		this.guild = guild;
		this.channelId = channelId;
		this.member = member;
		this.token = token;
		this.version = version;
		this.modules = modules;
		this.jda = jda;
	}

	public static Interaction fromJSON(Modules modules, DataObject json, JDA jda){
		if(!json.hasKey("guild_id")){
			return new Interaction(
				json.getLong("id"),
				InteractionType.get(json.getInt("type")),
				InteractionData.fromJSON(json.getObject("data")),
				null,
				json.getLong("channel_id"),
				null,
				json.getString("token"),
				json.getInt("version"),
				modules,
				jda
			);
		}

		var guildId = json.getLong("guild_id");
		var guild = modules.getGuildById(guildId);
		var entityBuilder = ((JDAImpl) modules.getJDA(guildId)).getEntityBuilder();
		return new Interaction(
			json.getLong("id"),
			InteractionType.get(json.getInt("type")),
			InteractionData.fromJSON(json.getObject("data")),
			guild,
			json.getLong("channel_id"),
			entityBuilder.createMember((GuildImpl) guild, json.getObject("member")),
			json.getString("token"),
			json.getInt("version"),
			modules,
			jda
		);
	}

	public long getId(){
		return this.id;
	}

	public long getChannelId(){
		return this.channelId;
	}

	public InteractionType getType(){
		return this.type;
	}

	public InteractionData getData(){
		return this.data;
	}

	public Guild getGuild(){
		return this.guild;
	}

	public Member getMember(){
		return this.member;
	}

	public String getToken(){
		return this.token;
	}

	public int getVersion(){
		return this.version;
	}

	public Modules getModules(){
		return this.modules;
	}

	public JDA getJDA(){
		return this.jda;
	}

}
