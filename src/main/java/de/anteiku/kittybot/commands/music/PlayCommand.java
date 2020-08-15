package de.anteiku.kittybot.commands.music;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Cache;
import de.anteiku.kittybot.objects.Emotes;
import de.anteiku.kittybot.objects.MusicPlayer;
import de.anteiku.kittybot.objects.ReactiveMessage;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.CommandContext;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class PlayCommand extends ACommand{

	public static final String COMMAND = "play";
	public static final String USAGE = "play <playlist/song/video>";
	public static final String DESCRIPTION = "Plays what you want him to play";
	protected static final String[] ALIAS = {"p", "spiele"};
	private static final int VOLUME_STEP = 10;

	public PlayCommand(){
		super(COMMAND, USAGE, DESCRIPTION, ALIAS);
	}

	@Override
	public void run(CommandContext ctx){
		if(ctx.getArgs().length == 0){
			sendError(ctx, "Please provide a link or search term");
			return;
		}
		GuildVoiceState voiceState = ctx.getMember().getVoiceState();
		if(voiceState != null && voiceState.inVoiceChannel()){
			var musicPlayer = Cache.getMusicPlayer(ctx.getGuild());
			if(musicPlayer == null){
				JdaLink link = KittyBot.getLavalink().getLink(ctx.getGuild());
				link.connect(voiceState.getChannel());

				LavalinkPlayer player = link.getPlayer();
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
		if(event.getReactionEmote().isEmoji()){
			var musicPlayer = Cache.getMusicPlayer(event.getGuild());
			if(musicPlayer == null){
				return;
			}
			if(!musicPlayer.getRequesterId().equals(event.getUserId())){
				event.getReaction().removeReaction(event.getUser()).queue();
				return;
			}
			String emoji = event.getReactionEmote().getEmoji();
			if(emoji.equals(Emotes.FORWARD.get())){
				musicPlayer.nextTrack();
			}
			else if(emoji.equals(Emotes.BACK.get())){
				musicPlayer.lastTrack();
			}
			else if(emoji.equals(Emotes.SHUFFLE.get())){
				musicPlayer.shuffle();
			}
			else if(emoji.equals(Emotes.PLAY_PAUSE.get())){
				musicPlayer.pause();
			}
			else if(emoji.equals(Emotes.VOLUME_DOWN.get())){
				musicPlayer.changeVolume(-VOLUME_STEP);
				//ctx.getChannel().editMessageById(ctx.getMessageId(), PlayCommand.buildMusicControlMessage(musicPlayer).build()).queue();
			}
			else if(emoji.equals(Emotes.VOLUME_UP.get())){
				musicPlayer.changeVolume(VOLUME_STEP);
				//ctx.getChannel().editMessageById(ctx.getMessageId(), PlayCommand.buildMusicControlMessage(musicPlayer).build()).queue();
			}
			else if(emoji.equals(Emotes.X.get())){
				event.getChannel().deleteMessageById(event.getMessageId()).queue();// TODO deleting the message is bad :)
				Cache.destroyMusicPlayer(event.getGuild(), event.getMessageId());
			}
			musicPlayer.updateMusicControlMessage(event.getChannel());
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

}
