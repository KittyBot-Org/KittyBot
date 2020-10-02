package de.kittybot.kittybot.events;

import de.kittybot.kittybot.objects.cache.GuildCache;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.RawGatewayEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnRawEvent extends ListenerAdapter {

    @Override
    public void onRawGateway(final @NotNull RawGatewayEvent event) { // this is topi's fucking idea don't blame me ok - cane
        if (!event.getType().equals("GUILD_MEMBER_UPDATE")){
            return;
        }
        final var json = event.getPayload();
        final var guildId = json.getString("guild_id");
        final var userId = json.getObject("user").getString("id");
        //noinspection ConstantConditions shut :)
        event.getJDA().getGuildById(guildId).retrieveMemberById(userId).queue(member ->{
            if (member.hasPermission(Permission.ADMINISTRATOR)){
                System.out.println("user cached");
                GuildCache.cacheGuildForUser(userId, guildId);
            }
            else {
                System.out.println("user uncached");
                GuildCache.uncacheGuildForUser(userId, guildId);
            }
        });
    }
}