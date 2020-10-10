package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.cache.MusicPlayerCache;
import de.kittybot.kittybot.objects.Emojis;
import de.kittybot.kittybot.objects.ReactiveMessage;
import de.kittybot.kittybot.objects.command.ACommand;
import de.kittybot.kittybot.objects.command.Category;
import de.kittybot.kittybot.objects.command.CommandContext;
import de.kittybot.kittybot.utils.AudioUtils;
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
		if(ctx.getArgs().length == 0){
			sendError(ctx, "Please provide a link or search term");
			return;
		}
		AudioUtils.checkVoiceChannel(ctx).thenAcceptAsync(connectFailureReason -> {
			if(connectFailureReason != null){
				sendError(ctx, "I can't play music as " + connectFailureReason.getReason() + ".");
				return;
			}
			final var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild(), true);
			final var channelId = musicPlayer.getChannelId();
			if(channelId == null){
				musicPlayer.loadQuery(this, ctx);
				return;
			}
			if(!ctx.getChannel().getId().equals(channelId)){
				return;
			}
			musicPlayer.loadQuery(this, ctx);
		});
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		var musicPlayer = MusicPlayerCache.getMusicPlayer(event.getGuild());
		if(musicPlayer == null){
			return;
		}
		var requester = musicPlayer.getRequesterId();
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
					musicPlayer.previousTrack();
					break;
				case Emojis.FORWARD:
					musicPlayer.nextTrack();
					break;
				case Emojis.SHUFFLE:
					musicPlayer.shuffle();
					break;
				case Emojis.VOLUME_DOWN:
					musicPlayer.changeVolume(-VOLUME_STEP);
					break;
				case Emojis.VOLUME_UP:
					musicPlayer.changeVolume(VOLUME_STEP);
					break;
				case Emojis.X:
					MusicPlayerCache.destroyMusicPlayer(event.getGuild());
					break;
				default:
			}
		}
		else if(event.getReactionEmote().getId().equals("744945002416963634")){
			musicPlayer.pause();
		}
		event.getReaction().removeReaction(event.getUser()).queue();
	}

}
