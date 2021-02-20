package de.kittybot.kittybot.slashcommands.interaction;

import club.minnced.discord.webhook.receive.ReadonlyMessage;
import de.kittybot.kittybot.modules.InteractionsModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.objects.exceptions.ModuleNotFoundException;
import de.kittybot.kittybot.objects.module.Module;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.slashcommands.interaction.response.FollowupMessage;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionRespondAction;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponse;
import de.kittybot.kittybot.slashcommands.interaction.response.InteractionResponseType;
import de.kittybot.kittybot.utils.Colors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.data.DataObject;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.GuildImpl;

import java.awt.Color;
import java.time.Instant;

public class Interaction{

	protected final long id, channelId;
	protected final InteractionType type;
	protected final InteractionData data;
	protected final String token;
	protected final int version;
	protected final User user;
	protected final MessageChannel channel;
	protected final Modules modules;
	protected final JDA jda;

	protected Interaction(DataObject json, User user, MessageChannel channel,  Modules modules, JDA jda){
		this.id = json.getLong("id");
		this.type = InteractionType.get(json.getInt("type"));
		this.data = InteractionData.fromJSON(json.getObject("data"));
		this.channelId = json.getLong("channel_id");
		this.token = json.getString("token");
		this.version = json.getInt("version");
		this.user = user;
		this.channel = channel;
		this.modules = modules;
		this.jda = jda;
	}

	public static Interaction fromJSON(Modules modules, DataObject json, JDA jda){
		var entityBuilder = ((JDAImpl) jda).getEntityBuilder();

		if(!json.hasKey("guild_id")){
			return new Interaction(
				json,
				entityBuilder.createUser(json.getObject("user")),
				jda.getPrivateChannelById(json.getLong("channel_id")),
				modules,
				jda
			);
		}

		var guildId = json.getLong("guild_id");
		var guild = (GuildImpl) jda.getGuildById(guildId);
		if(guild == null){
			throw new RuntimeException("Guild for interaction not found");
		}
		return new GuildInteraction(
			json,
			guild,
			entityBuilder.createMember(guild, json.getObject("member")),
			guild.getTextChannelById(json.getLong("channel_id")),
			modules,
			jda
		);
	}

	public boolean isFromGuild(){
		return this instanceof GuildInteraction;
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

	public String getToken(){
		return this.token;
	}

	public int getVersion(){
		return this.version;
	}

	public User getUser(){
		return this.user;
	}

	public long getUserId(){
		return this.user.getIdLong();
	}

	public User getSelfUser(){
		return this.jda.getSelfUser();
	}

	public MessageChannel getChannel(){
		return this.channel;
	}

	public Modules getModules(){
		return this.modules;
	}

	public <T extends Module> T get(Class<T> clazz){
		return this.modules.get(clazz);
	}

	public JDA getJDA(){
		return this.jda;
	}

	public EmbedBuilder getEmbed(){
		return applyDefaultStyle(new EmbedBuilder());
	}

	public EmbedBuilder applyDefaultStyle(EmbedBuilder embedBuilder){
		String name;
		if(this instanceof GuildInteraction){
			name = ((GuildInteraction) this).getMember().getEffectiveName();
		}
		else{
			name = user.getName();
		}
		return embedBuilder.setFooter(name, user.getEffectiveAvatarUrl()).setTimestamp(Instant.now());
	}

	public EmbedBuilder getSuccessEmbed(){
		return getEmbed().setColor(Colors.KITTYBOT_BLUE);
	}

	public void reply(String message){
		this.modules.get(InteractionsModule.class).reply(this).embeds(getSuccessEmbed().setDescription(message).build()).queue();
	}

	public void reply(EmbedBuilder embed){
		reply(applyDefaultStyle(embed).build());
	}

	public void reply(MessageEmbed embed){
		this.modules.get(InteractionsModule.class).reply(this).embeds(embed).queue();
	}

	public void reply(InteractionResponse response){
		this.modules.get(InteractionsModule.class).reply(this).fromData(response).queue();
	}

	public void reply(InteractionResponse data, boolean withSource){
		this.modules.get(InteractionsModule.class).reply(this, withSource).fromData(data).queue();
	}

	public void error(String error){
		this.modules.get(InteractionsModule.class).reply(this).type(InteractionResponseType.ACKNOWLEDGE).content(Emoji.X.get() + " " + error).ephemeral().queue();
	}

	public void followup(String message){
		followup(new FollowupMessage.Builder().setEmbeds(getSuccessEmbed().setDescription(message).build()).build());
	}

	public void followup(FollowupMessage message){
		followupMessage(message).queue();
	}

	public RestAction<ReadonlyMessage> followupMessage(FollowupMessage message){
		return this.modules.get(InteractionsModule.class).followup(this, message);
	}

	public void followupError(String error){
		followup(new FollowupMessage.Builder().setEmbeds(getErrorEmbed().setDescription(error).build()).build());
	}

	public EmbedBuilder getErrorEmbed(){
		return getEmbed().setColor(Color.RED);
	}

	public void sendAcknowledge(){
		sendAcknowledge(true);
	}

	public void sendAcknowledge(boolean withSource){
		acknowledge(withSource).queue();
	}

	public InteractionRespondAction acknowledge(boolean withSource){
		return this.modules.get(InteractionsModule.class).acknowledge(this, withSource);
	}

}
