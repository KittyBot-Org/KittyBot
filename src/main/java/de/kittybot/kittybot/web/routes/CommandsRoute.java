package de.kittybot.kittybot.web.routes;

import de.kittybot.kittybot.command.Category;
import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandsRoute implements Handler{

	private final KittyBot main;

	public CommandsRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var commandSet = this.main.getCommandManager().getCommands();
		var categories = DataArray.fromCollection(
				Arrays.stream(Category.values()).map(category ->
						DataObject.empty().put("name", category.getName()).put("emote_url", category.getEmoteUrl()).put("commands", DataArray.fromCollection(
								commandSet.stream().filter(cmd -> cmd.getCategory() == category).map(cmd ->
										DataObject.empty().put("command", cmd.getCommand()).put("usage", cmd.getRawUsage()).put("description", cmd.getDescription())
								).collect(Collectors.toSet())
						))
				).collect(Collectors.toSet())
		);
		WebService.ok(ctx, DataObject.empty().put("prefix", this.main.getConfig().getString("default_prefix")).put("categories", categories));
	}


}