package de.kittybot.kittybot.slashcommands;

import dataflow.analysis.Store;
import de.kittybot.kittybot.utils.TimeUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.EmoteImpl;
import org.w3c.dom.Text;

import javax.annotation.CheckReturnValue;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Options{

	private final Map<String, SlashCommandEvent.OptionData> options;
	private final JDA jda;

	public Options(List<SlashCommandEvent.OptionData> options, JDA jda){
		this.options = options.stream().collect(Collectors.toMap(SlashCommandEvent.OptionData::getName, Function.identity()));
		this.jda = jda;
	}

	public Map<String, SlashCommandEvent.OptionData> getMap(){
		return this.options;
	}

	public Stream<SlashCommandEvent.OptionData> stream(){
		return this.options.values().stream();
	}

	public boolean has(String name){
		return this.options.containsKey(name);
	}

	public SlashCommandEvent.OptionData get(String name){
		return this.options.get(name);
	}

	private <T> T getOrDefault(String name, Function<String, T> mapper, T defaultValue){
		if(!has(name)){
			return defaultValue;
		}
		var value = mapper.apply(name);
		return value == null ? value : defaultValue;
	}

	@CheckReturnValue
	public String getString(String name){
		return get(name).getAsString();
	}
	public String getString(String name, String defaultString){
		return getOrDefault(name, this::getString, defaultString);
	}

	@CheckReturnValue
	public Long getLong(String name){
		return get(name).getAsLong();
	}
	public long getLong(String name, long defaultLong){
		return getOrDefault(name, this::getLong, defaultLong);
	}

	@CheckReturnValue
	public int getInt(String name){
		return (int) get(name).getAsLong();
	}
	public int getInt(String name, int defaultInt){
		return getOrDefault(name, this::getInt, defaultInt);
	}

	@CheckReturnValue
	public float getFloat(String name){
		return Float.parseFloat(get(name).getAsString());
	}
	public float getFloat(String name, float defaultFloat){
		return getOrDefault(name, this::getFloat, defaultFloat);
	}

	@CheckReturnValue
	public boolean getBoolean(String name){
		return get(name).getAsBoolean();
	}
	public boolean getBoolean(String name, boolean defaultBoolean){
		return getOrDefault(name, this::getBoolean, defaultBoolean);
	}

	@CheckReturnValue
	public Member getMember(String name){
		return this.options.get(name).getAsMember();
	}
	public Member getMember(String name, Member defaultMember){
		return getOrDefault(name, this::getMember, defaultMember);
	}

	@CheckReturnValue
	public User getUser(String name){
		return get(name).getAsUser();
	}
	public User getUser(String name, User defaultUser){
		return getOrDefault(name, this::getUser, defaultUser);
	}

	@CheckReturnValue
	public PrivateChannel getPrivateChannel(String name){
		return get(name).getAsPrivateChannel();
	}
	public PrivateChannel getPrivateChannel(String name, PrivateChannel defaultPrivateChannel){
		return getOrDefault(name, this::getPrivateChannel, defaultPrivateChannel);
	}

	@CheckReturnValue
	public MessageChannel getMessageChannel(String name){
		return get(name).getAsMessageChannel();
	}
	public MessageChannel getMessageChannel(String name, MessageChannel defaultMessageChannel){
		return getOrDefault(name, this::getMessageChannel, defaultMessageChannel);
	}

	@CheckReturnValue
	public GuildChannel getGuildChannel(String name){
		return get(name).getAsGuildChannel();
	}
	public GuildChannel getGuildChannel(String name, GuildChannel defaultGuildChannel){
		return getOrDefault(name, this::getGuildChannel, defaultGuildChannel);
	}

	@CheckReturnValue
	public TextChannel getTextChannel(String name){
		var channel = get(name).getAsGuildChannel();
		if(channel == null){
			return null;
		}
		return channel instanceof TextChannel ? (TextChannel) channel : null;
	}
	public TextChannel getTextChannel(String name, TextChannel defaultTextChannel){
		return getOrDefault(name, this::getTextChannel, defaultTextChannel);
	}

	@CheckReturnValue
	public VoiceChannel getVoiceChannel(String name){
		var channel = get(name).getAsGuildChannel();
		if(channel == null){
			return null;
		}
		return channel instanceof VoiceChannel ? (VoiceChannel) channel : null;
	}
	public VoiceChannel getVoiceChannel(String name, VoiceChannel defaultVoiceChannel){
		return getOrDefault(name, this::getVoiceChannel, defaultVoiceChannel);
	}

	@CheckReturnValue
	public StoreChannel getStoreChannel(String name){
		var channel = get(name).getAsGuildChannel();
		if(channel == null){
			return null;
		}
		return channel instanceof StoreChannel ? (StoreChannel) channel : null;
	}
	public StoreChannel getStoreChannel(String name, StoreChannel defaultStoreChannel){
		return getOrDefault(name, this::getStoreChannel, defaultStoreChannel);
	}

	@CheckReturnValue
	public Category getCategory(String name){
		var channel = get(name).getAsGuildChannel();
		if(channel == null){
			return null;
		}
		return channel instanceof Category ? (Category) channel : null;
	}
	public Category getCategory(String name, Category defaultCategory){
		return getOrDefault(name, this::getCategory, defaultCategory);
	}

	public Role getRole(String name){
		return get(name).getAsRole();
	}
	public Role getRole(String name, Role defaultRole){
		return getOrDefault(name, this::getRole, defaultRole);
	}

	public LocalDateTime getTime(String name){
		return TimeUtils.parse(getString(name));
	}
	public LocalDateTime getTime(String name, LocalDateTime defaultLocalDateTime){
		return getOrDefault(name, this::getTime, defaultLocalDateTime);
	}

	public Emote getEmote(String name){
		try{
			var rawEmote = getString(name);
			var matcher = Message.MentionType.EMOTE.getPattern().matcher(rawEmote);
			if(matcher.matches()){
				long emoteId = MiscUtil.parseSnowflake(matcher.group(2));
				var emote = this.jda.getEmoteById(emoteId);
				if(emote == null){
					emote = new EmoteImpl(emoteId, (JDAImpl) jda).setName(matcher.group(1)).setAnimated(matcher.group(0).startsWith("<a:"));
				}
				return emote;
			}
		}
		catch(NumberFormatException ignored){
		}
		return null;
	}
	public Emote getEmote(String name, Emote defaultEmote){
		return getOrDefault(name, this::getEmote, defaultEmote);
	}



}
