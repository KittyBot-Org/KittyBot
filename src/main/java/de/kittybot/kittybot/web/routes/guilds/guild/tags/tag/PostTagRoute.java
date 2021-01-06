package de.kittybot.kittybot.web.routes.guilds.guild.tags.tag;

import de.kittybot.kittybot.module.Modules;
import de.kittybot.kittybot.web.WebService;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.http.InternalServerErrorResponse;
import net.dv8tion.jda.api.utils.data.DataObject;
import org.jetbrains.annotations.NotNull;

public class PostTagRoute implements Handler{

	private final Modules modules;

	public PostTagRoute(Modules modules){
		this.modules = modules;
	}

	@Override
	public void handle(@NotNull Context ctx){
		var tagId = this.modules.getWebService().getTagId(ctx);
		var json = DataObject.fromJson(ctx.body());
		var name = json.getString("name", "");
		var content = json.getString("content", "");
		var userId = json.getLong("user_id", -1);
		if(name.isBlank() || content.isBlank() || userId == -1){
			throw new BadRequestResponse("Please provide a valid name, content or userId");
		}
		if(!this.modules.getTagModule().edit(tagId, name, content, userId)){
			throw new InternalServerErrorResponse("Error while updating tag");
		}
		WebService.accepted(ctx);
	}

}
