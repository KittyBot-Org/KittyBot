package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emojis;
import de.anteiku.kittybot.objects.MusicPlayer;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class PlayCommand extends ACommand{

	public static final String COMMAND = "play";
	public static final String USAGE = "play <playlist/song/video>";
	public static final String DESCRIPTION = "Plays what you want Kitty to play";
	protected static final String[] ALIAS = {"p", "spiele"};
	protected static final Category CATEGORY = Category.MUSIC;
	private static final int VOLUME_STEP = 10;

	public PlayCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS, CATEGORY);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendError(ctx, "Please provide a link or search term");
			return;
		}
		var voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.inVoiceChannel()){
			var musicPlayer = Cache.getMusicPlayer(ctx.getGuild());
			if(musicPlayer == null){
				var link = KittyBot.getLavalink().getLink(ctx.getGuild());
				link.connect(voiceState.getChannel());

				var player = link.getPlayer();
				musicPlayer = new MusicPlayer(player);
				player.addListener(musicPlayer);
				Cache.addMusicPlayer(ctx.getGuild(), musicPlayer);
			}
			musicPlayer.loadItem(this, ctx, ctx.getArgs());
		}
		else{
			sendError(ctx, "Please connect to a voice channel to play some stuff");
		}
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		var musicPlayer = Cache.getMusicPlayer(event.getGuild());
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
					Cache.destroyMusicPlayer(event.getGuild());
					break;
				default:
			}
		}
		else if(event.getReactionEmote().getId().equals("744945002416963634")){
			musicPlayer.pause();
		}
		musicPlayer.updateMusicControlMessage(event.getChannel());
		event.getReaction().removeReaction(event.getUser()).queue();
	}

}
