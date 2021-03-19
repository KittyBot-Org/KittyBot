package de.kittybot.kittybot.web.commands;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
import de.kittybot.kittybot.slashcommands.application.Category;
import de.kittybot.kittybot.slashcommands.application.Command;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GetCommandsRoute implements Handler{

	private final Modules modules;

	public GetCommandsRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){

		var categories = DataArray.fromCollection(Arrays.stream(Category.values()).map(Category::toJSON).collect(Collectors.toList()));
		var commands = DataArray.fromCollection(this.modules.get(CommandsModule.class).getCommands().values().stream().map(Command::toJSON).collect(Collectors.toList()));

		WebModule.ok(ctx, DataObject.empty().put("categories", categories).put("commands", commands));
	}


}
