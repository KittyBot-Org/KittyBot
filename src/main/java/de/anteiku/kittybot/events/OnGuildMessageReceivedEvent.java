package de.anteiku.kittybot.events;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.utils.Emotes;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.Instant;

public class OnGuildMessageReceivedEvent extends ListenerAdapter{
	
	private final KittyBot main;
	
	public OnGuildMessageReceivedEvent(KittyBot main){
		this.main = main;
	}
	
	@Override
	public void onGuildMessageReceived(GuildMessageReceivedEvent event){
		if(event.getAuthor().isBot() || event.getAuthor().isFake()){
			return;
		}
		if(! main.commandManager.checkCommands(event)){
			if(event.getMessage().getMentionedUsers().size() == 1 && event.getMessage().getMentionedUsers().get(0).getId().equals(main.jda.getSelfUser().getId())){
				event.getMessage().addReaction(Emotes.QUESTION.get()).queue();
				event.getChannel().sendMessage(new EmbedBuilder().setColor(Color.ORANGE).setTitle("Do you need help?").setDescription("My current prefix for this guild is `" + main.database.getCommandPrefix(event.getGuild().getId()) + "`\n" + "If you don't like my prefix you can ping me directly!\n" + "To have a look at all my commands use `" + main.database.getCommandPrefix(event.getGuild().getId()) + "cmds`\n" + "To get help use`" + main.database.getCommandPrefix(event.getGuild().getId()) + "help`").setThumbnail(event.getJDA().getSelfUser().getAvatarUrl()).setFooter(event.getMember().getEffectiveName(), event.getAuthor().getEffectiveAvatarUrl()).setTimestamp(Instant.now()).build()).queue();
			}
		}
	}
	
}
