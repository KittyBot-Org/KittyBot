package de.kittybot.kittybot.web.routes.guilds.guild.tags.tag;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import org.jetbrains.annotations.NotNull;

public class DeleteTagRoute implements Handler{

	private final Modules modules;

	public DeleteTagRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		if(!this.modules.get(TagsModule.class).delete(this.modules.get(WebService.class).getTagId(ctx))){
			throw new InternalServerErrorResponse("Error while deleting tag");
		}
		WebService.accepted(ctx);
	}

}
