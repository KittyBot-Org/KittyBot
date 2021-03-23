package de.kittybot.kittybot.commands.info.info;

import de.kittybot.kittybot.objects.enums.BotList;
import de.kittybot.kittybot.slashcommands.application.options.SubCommand;
import de.kittybot.kittybot.slashcommands.interaction.Interaction;
import de.kittybot.kittybot.slashcommands.interaction.Options;
import de.kittybot.kittybot.utils.MessageUtils;

import java.util.Arrays;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class VoteCommand extends SubCommand{

	public VoteCommand(){
		super("vote", "Displays all info about voting for kitty");
	}

	@Override
	public void run(Options options, Interaction ia){
		ia.reply("You can vote on following sites for KittyBot:\n" + Arrays.stream(BotList.values()).
			filter(BotList::canVote)
			.map(botList -> MessageUtils.maskLink(botList.getName(), botList.getBotUrl())).collect(Collectors.joining(", "))
		);
	}

}