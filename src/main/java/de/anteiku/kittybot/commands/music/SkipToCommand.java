package de.anteiku.kittybot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;

import java.util.List;

public class SkipToCommand extends ACommand{

    public static final String COMMAND = "skipto";
    public static final String USAGE = "skipto <position>";
    public static final String DESCRIPTION = "Skips to a track with given position";
    protected static final String[] ALIASES = {"jumpto"};
    protected static final Category CATEGORY = Category.MUSIC;

    public SkipToCommand(){
        super(COMMAND, USAGE, DESCRIPTION, ALIASES, CATEGORY);
    }

    @Override
    public void run(CommandContext ctx){
        var voiceState = ctx.getMember().getVoiceState();
        if(voiceState != null && !voiceState.inVoiceChannel()){
            sendError(ctx, "To use this command you need to be connected to a voice channel");
            return;
        }
        var musicPlayer = MusicPlayerCache.getMusicPlayer(ctx.getGuild());
        if(musicPlayer == null){
            sendError(ctx, "No active music player found!");
            return;
        }
        var queue = musicPlayer.getQueue();
        if (queue.isEmpty()){
            sendError(ctx, "There are currently no tracks queued");
            return;
        }
        var args = ctx.getArgs();
        if (args.length == 0){
            queue.clear();
            sendAnswer(ctx, "The queue has been cleared");
            return;
        }
        var position = 0;
        try{
            position = Integer.parseUnsignedInt(args[0]);
        }
        catch (NumberFormatException ex){
            sendError(ctx, "Please provide a valid number");
            return;
        }
        if (position == 0){
            sendError(ctx, "PLease enter a position bigger than 0");
            return;
        }
        if (position > queue.size()){
            sendError(ctx, "The position you entered is bigger than the queue size");
            return;
        }
        // TODO add check for dj role
        position--;
        var casted = ((List<?>) queue);
        var track = ((AudioTrack) casted.get(position));
        musicPlayer.getPlayer().playTrack(track);
        casted.remove(position);
    }

}
