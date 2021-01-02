package de.kittybot.kittybot.web.routes.guilds.guild.tags.tag;

import de.kittybot.kittybot.main.KittyBot;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import net.dv8tion.jda.api.utils.data.DataArray;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Collectors;

public class DeleteTagRoute implements Handler{

	private final KittyBot main;

	public DeleteTagRoute(KittyBot main){
		this.main = main;
	}

	@Override
	public void handle(@NotNull Context ctx){
		if(!this.main.getTagManager().delete(this.main.getWebService().getTagId(ctx))){
			throw new InternalServerErrorResponse("Error while deleting tag");
		}
		WebService.accepted(ctx);
	}

}
