package de.kittybot.kittybot.commands.admin.settings;

import de.kittybot.kittybot.modules.SettingsModule;
import de.kittybot.kittybot.objects.enums.Emoji;
import de.kittybot.kittybot.slashcommands.application.options.GuildSubCommand;
import de.kittybot.kittybot.slashcommands.GuildCommandContext;
import de.kittybot.kittybot.slashcommands.Options;
import de.kittybot.kittybot.utils.Config;
import de.kittybot.kittybot.utils.MessageUtils;

public class ListCommand extends GuildSubCommand{

	public ListCommand(){
		super("list", "Lists the current settings");
	}

	@Override
	public void run(Options options, GuildCommandContext ctx){
		var guildId = ctx.getGuildId();
		var settings = ctx.get(SettingsModule.class).getSettings(guildId);
		ctx.reply(builder -> builder
				.setAuthor("Guild settings:", Config.ORIGIN_URL + "/guilds/" + guildId + "/dashboard", Emoji.SETTINGS.getUrl())
				.addField("Announcement Channel: ", settings.getAnnouncementChannel(), false)
				.addField("Join Messages: " + MessageUtils.getBoolEmote(settings.areJoinMessagesEnabled()), settings.getJoinMessage(), false)
				.addField("Leave Messages: " + MessageUtils.getBoolEmote(settings.areLeaveMessagesEnabled()), settings.getLeaveMessage(), false)
				.addField("Stream Announcement Channel:", settings.getStreamAnnouncementChannel(), false)
				.addField("DJ Role: ", settings.getDjRole(), false)
				.addField("NSFW Enabled: ", MessageUtils.getBoolEmote(settings.isNsfwEnabled()), false)
				.addField("Log Messages: " + MessageUtils.getBoolEmote(settings.areLogMessagesEnabled()), settings.getLogChannel(), false)
				.addField("Snipes Enabled:", MessageUtils.getBoolEmote(settings.areSnipesEnabled()), false)
				.addField("Role Saver Enabled:", MessageUtils.getBoolEmote(settings.isRoleSaverEnabled()), false)
			//.addField("Inactive Role: " + TimeUtils.formatDurationDHMS(settings.getInactiveDuration()), settings.getLogChannel(), false)
		);
	}

}
