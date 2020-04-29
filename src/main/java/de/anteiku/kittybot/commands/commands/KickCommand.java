package de.anteiku.kittybot.commands.commands;

import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.commands.ACommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.Arrays;
import java.util.List;

public class KickCommand extends ACommand{

	public static String COMMAND = "kick";
	public static String USAGE = "kick <@user, ...> <reason>";
	public static String DESCRIPTION = "Kicks the given users with a reason";
	public static Permission[] PERMISSIONS = {Permission.KICK_MEMBERS};
	protected static String[] ALIAS = {};

	public KickCommand(KittyBot main){
		super(main, COMMAND, USAGE, DESCRIPTION, ALIAS, PERMISSIONS);
	}

	@Override
	public void run(String[] args, GuildMessageReceivedEvent event){
		List<Member> members = event.getMessage().getMentionedMembers();
		if(members.size() == 0){
			sendError(event, "Please mention one or more user");
			return;
		}
		String reason = String.join(" ", Arrays.copyOfRange(args, members.size() - 1, args.length));
		for(Member member : members){
			if(event.getMember().canInteract(member)){
				member.kick(reason).queue(
					null,
					failure -> sendError(event, "Error while kicking " + member.getAsMention())
				);
			}
			else{
				sendError(event, "Sorry you don't have the permission to kick " + member.getAsMention());
			}
		}
		sendAnswer(event, "All kicks processed");
	}

}
