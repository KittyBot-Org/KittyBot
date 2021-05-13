package de.kittybot.kittybot.commands.music;

import de.kittybot.kittybot.modules.MusicModule;
import de.kittybot.kittybot.modules.GuildSettingsModule;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.RunGuildCommand;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionBoolean;
import de.kittybot.kittybot.slashcommands.application.options.CommandOptionInteger;
import de.kittybot.kittybot.slashcommands.interaction.GuildInteraction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MusicUtils;
import net.dv8tion.jda.api.Permission;

@SuppressWarnings("unused")
public class RemoveCommand extends RunGuildCommand{

	public RemoveCommand(){
		super("remove", "Removes songs from the queue", Category.MUSIC);
		addOptions(
			new CommandOptionBoolean("all", "Queue index inclusive to delete until. Omit for from+1"),
			new CommandOptionInteger("from", "Deletes all queued songs"),
			new CommandOptionInteger("to", "Queue index inclusive to delete until. Omit for from+1")
		);
	}

	@Override
	public void run(Options options, GuildInteraction ia){
		var scheduler = ia.get(MusicModule.class).getScheduler(ia.getGuildId());
		var queue = scheduler.getQueue();
		if(queue.isEmpty()){
			ia.error("The queue is empty. Nothing to remove");
			return;
		}
		if(!MusicUtils.checkCommandRequirements(ia, scheduler)){
			return;
		}
		if(options.has("all") && options.getBoolean("all")){
			var member = ia.getMember();
			if(!member.hasPermission(Permission.ADMINISTRATOR) && !ia.get(GuildSettingsModule.class).hasDJRole(member)){
				ia.error("You need to be the dj");
				return;
			}
			scheduler.getQueue().clear();
			ia.reply("Removed all queued songs");
			return;
		}
		if(!options.has("from")){
			ia.error("Please specify from");
			return;
		}
		var from = options.getInt("from");
		var to = options.has("to") ? options.getInt("to") : from;

		var removed = scheduler.removeQueue(from, to, ia.getMember());
		ia.reply("Removed " + removed + " " + (removed > 1 ? "entries" : "entry"));
	}

}
