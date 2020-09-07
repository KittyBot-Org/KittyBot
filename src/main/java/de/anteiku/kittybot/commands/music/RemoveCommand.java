package de.anteiku.kittybot.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import de.anteiku.kittybot.objects.cache.MusicPlayerCache;
import de.anteiku.kittybot.objects.command.ACommand;
import de.anteiku.kittybot.objects.command.Category;
import de.anteiku.kittybot.objects.command.CommandContext;
import de.anteiku.kittybot.utils.Utils;

import java.util.List;

public class RemoveCommand extends ACommand{

    public static final String COMMAND = "remove";
    public static final String USAGE = "remove <position>";
    public static final String DESCRIPTION = "Removes a track with given position from the queue";
    protected static final String[] ALIASES = {};
    protected static final Category CATEGORY = Category.MUSIC;

    public RemoveCommand(){
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
        position--;
        var casted = ((List<?>) queue);
        var track = ((AudioTrack) casted.get(position));
        if (!track.getUserData(String.class).equals(ctx.getUser().getId())){
            sendError(ctx, "You have to be the requester of the track in order to remove it from the queue");
            return;
        }
        sendAnswer(ctx, "Track " + Utils.formatTrackTitle(track)
                + " with position **" + (position + 1) + "** has been removed from the queue.");
        casted.remove(position);
    }

}
