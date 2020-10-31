package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicManagerCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.objects.data.ReactiveMessage;
import de.kittybot.kittybot.objects.music.AudioLoader;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class PlayCommand extends ACommand{

	public static final String COMMAND = "play";
	public static final String USAGE = "play <playlist/song/video>";
	public static final String DESCRIPTION = "Plays what you want Kitty to play";
	protected static final String[] ALIASES = {"p", "spiele"};
	protected static final Category CATEGORY = Category.MUSIC;
	private static final int VOLUME_STEP = 10;

	public PlayCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		final var args = ctx.getArgs();
		if(args.length == 0){
			sendError(ctx, "Please provide a link or search term");
			return;
		}
		final var connectionFailure = MusicUtils.checkVoiceChannel(ctx);
		if(connectionFailure != null){
			sendError(ctx, "I can't play music as " + connectionFailure.getReason());
			return;
		}
		AudioLoader.loadQuery(String.join(" ", args), ctx.getUser().getId(), ctx.getGuild(), ctx.getChannel());
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		var musicManager = MusicManagerCache.getMusicManager(event.getGuild());
		if(musicManager == null){
			return;
		}
		var requester = musicManager.getRequesterId();
		if(requester == null){
			return;
		}
		if(!requester.equals(event.getUserId())){
			event.getReaction().removeReaction(event.getUser()).queue();
			return;
		}
		if(event.getReactionEmote().isEmoji()){
			var emoji = event.getReactionEmote().getEmoji();
			switch(emoji){
				case Emojis.BACK:
					musicManager.previousTrack();
					break;
				case Emojis.FORWARD:
					musicManager.nextTrack();
					break;
				case Emojis.SHUFFLE:
					musicManager.shuffle();
					break;
				case Emojis.VOLUME_DOWN:
					musicManager.setVolume(-VOLUME_STEP);
					break;
				case Emojis.VOLUME_UP:
					musicManager.setVolume(VOLUME_STEP);
					break;
				case Emojis.X:
					MusicManagerCache.destroyMusicManager(event.getGuild());
					break;
				default:
			}
		}
		else if(event.getReactionEmote().getId().equals("744945002416963634")){
			musicManager.pause();
		}
		musicManager.updateMusicControlMessage(event.getChannel());
		event.getReaction().removeReaction(event.getUser()).queue();
	}

}
