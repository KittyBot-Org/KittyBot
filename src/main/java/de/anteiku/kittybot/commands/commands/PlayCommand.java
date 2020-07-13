package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import de.anteiku.kittybot.commands.MusicPlayer;
import de.anteiku.kittybot.utils.Emotes;
import de.anteiku.kittybot.utils.ReactiveMessage;
import lavalink.client.io.jda.JdaLink;
import lavalink.client.player.LavalinkPlayer;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class PlayCommand extends ACommand{

	private static final int VOLUME_STEP = 10;
	public static String COMMAND = "play";
	public static String USAGE = "play <playlist/song/video>";
	public static String DESCRIPTION = "Plays what you want him to play";
	protected static String[] ALIAS = {"p", "spiele"};

	public PlayCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS);
		this.main = main;
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		GuildVoiceState voiceState = event.getMember().getVoiceState();
		if(voiceState != null && voiceState.inVoiceChannel()){
			JdaLink link = main.lavalink.getLink(event.getGuild());
			link.connect(voiceState.getChannel());

			LavalinkPlayer player = link.getPlayer();
			MusicPlayer musicPlayer = new MusicPlayer(player);
			player.addListener(musicPlayer);
			main.commandManager.addMusicPlayer(event.getGuild(), musicPlayer);
			musicPlayer.loadItem(this, event, args);
		}
		else{
			sendError(event, "Please connect to a voice channel to play some stuff");
		}
	}

	@Override
	public void reactionAdd(ReactiveMessage reactiveMessage, GuildMessageReactionAddEvent event){
		if(event.getReactionEmote().isEmoji()){
			var musicPlayer = main.commandManager.getMusicPlayer(event.getGuild());
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
				//event.getChannel().editMessageById(event.getMessageId(), PlayCommand.buildMusicControlMessage(musicPlayer).build()).queue();
			}
			else if(emoji.equals(Emotes.VOLUME_UP.get())){
				musicPlayer.changeVolume(VOLUME_STEP);
				//event.getChannel().editMessageById(event.getMessageId(), PlayCommand.buildMusicControlMessage(musicPlayer).build()).queue();
			}
			else if(emoji.equals(Emotes.X.get())){
				event.getChannel().deleteMessageById(event.getMessageId()).queue();// TODO deleting the message is bad :)
				main.commandManager.destroyMusicPlayer(event.getGuild(), event.getMessageId());
			}
			musicPlayer.updateMusicControlMessage(event.getChannel(), event.getMember());
			event.getReaction().removeReaction(event.getUser()).queue();
		}
	}

}
