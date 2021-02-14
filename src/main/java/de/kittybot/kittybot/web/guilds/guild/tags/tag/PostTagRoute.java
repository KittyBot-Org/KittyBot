package de.kittybot.kittybot.web.guilds.guild.tags.tag;

import de.kittybot.kittybot.modules.TagsModule;
import de.kittybot.kittybot.modules.WebModule;
import de.kittybot.kittybot.objects.module.Modules;
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
		var tagId = this.modules.get(WebModule.class).getTagId(ctx);
		var json = DataObject.fromJson(ctx.body());
		var name = json.getString("name", "");
		var content = json.getString("content", "");
		var userId = json.getLong("user_id", -1);
		if(name.isBlank() || content.isBlank() || userId == -1){
			throw new BadRequestResponse("Please provide a valid name, content or userId");
		}
		if(!this.modules.get(TagsModule.class).edit(tagId, name, content, userId)){
			throw new InternalServerErrorResponse("Error while updating tag");
		}
		WebModule.accepted(ctx);
	}

}
