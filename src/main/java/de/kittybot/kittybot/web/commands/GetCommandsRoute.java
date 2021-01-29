package de.kittybot.kittybot.web.commands;

import de.kittybot.kittybot.modules.CommandsModule;
import de.kittybot.kittybot.modules.WebService;
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

		WebService.ok(ctx, DataObject.empty().put("categories", DataArray.fromCollection(
			this.modules.get(CommandsModule.class).getCommands().values().stream().collect(Collectors.groupingBy(Command::getCategory))
				.entrySet().stream()
				.map(entry ->
					entry.getKey().toJSON()
						.put("commands", DataArray.fromCollection(
							entry.getValue().stream()
								.map(Command::toJSON)
								.collect(Collectors.toList())
						))
				)
				.collect(Collectors.toList())
		)));
	}


}
