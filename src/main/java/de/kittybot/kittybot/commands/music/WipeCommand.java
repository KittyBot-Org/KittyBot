package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import de.kittybot.kittybot.slashcommands.application.RunnableCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.context.CommandContext;
import de.kittybot.kittybot.slashcommands.context.Options;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class WipeCommand extends Command implements RunnableCommand{

	public WipeCommand(){
		super("wipe", "Wipes songs from the queue", Category.MUSIC);
		addOptions(
			new CommandOptionBoolean("all", "Queue index inclusive to delete until. Omit for from+1"),
			new CommandOptionInteger("from", "Deletes all queued songs"),
			new CommandOptionInteger("to", "Queue index inclusive to delete until. Omit for from+1")
		);
	}

	@Override
	public void run(Options options, CommandContext ctx){
		var player = ctx.get(MusicModule.class).get(ctx.getGuildId());
		var queue = player.getQueue();
		if(queue.isEmpty()){
			ctx.error("The queue is empty. Nothing to remove");
			return;
		}
		if(!MusicUtils.checkCommandRequirements(ctx, player)){
			return;
		}
		if(options.has("all") && options.getBoolean("all")){
			var member = ctx.getMember();
			if(!member.hasPermission(Permission.ADMINISTRATOR) && !ctx.get(SettingsModule.class).hasDJRole(member)){
				ctx.error("You need to be the dj");
				return;
			}
			var removed = player.removeQueue(0, queue.size() - 1, ctx.getMember());
			ctx.reply("Removed all queued songs");
			return;
		}
		if(!options.has("from")){
			ctx.error("Please specify from");
			return;
		}
		var from = options.getInt("from");
		var to = options.has("to") ? options.getInt("to") : from;

		var removed = player.removeQueue(from, to, ctx.getMember());
		ctx.reply("Removed " + removed + " " + (removed > 1 ? "entries" : "entry"));
	}

}
