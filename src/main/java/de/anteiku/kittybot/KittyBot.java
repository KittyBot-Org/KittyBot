package de.anteiku.kittybot;

import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import de.anteiku.kittybot.database.Database;
import de.anteiku.kittybot.database.SQL;
import de.anteiku.kittybot.events.*;
import de.anteiku.kittybot.objects.Config;
import de.anteiku.kittybot.objects.LavalinkNode;
import de.anteiku.kittybot.objects.cache.MessageCache;
import de.anteiku.kittybot.objects.command.CommandManager;
import de.anteiku.kittybot.objects.paginator.Paginator;
import lavalink.client.io.Link;
import lavalink.client.io.jda.JdaLavalink;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.OkHttpClient;
import org.discordbots.api.client.DiscordBotListAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.net.URI;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class KittyBot {

    public static final DateTimeFormatter TIME_IN_CENTRAL_EUROPE = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss z").withZone(ZoneId.of("Europe/Berlin"));
    private static final Logger LOG = LoggerFactory.getLogger(KittyBot.class);
    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient();
    private static final AudioPlayerManager AUDIO_PLAYER_MANAGER = new DefaultAudioPlayerManager();
    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final EventWaiter WAITER = new EventWaiter();
    private static JdaLavalink lavalink;
    private static JDA jda;
    private static DiscordBotListAPI discordBotListAPI;

    public KittyBot() {
        LOG.info("\n" + "\n" + "         _   ___ _   _        ______       _   \n" + "        | | / (_) | | |       | ___ \\     | |  \n" + "        | |/ / _| |_| |_ _   _| |_/ / ___ | |_ \n" + "        |    \\| | __| __| | | | ___ \\/ _ \\| __|\n" + "        | |\\  \\ | |_| |_| |_| | |_/ / (_) | |_ \n" + "        \\_| \\_/_|\\__|\\__|\\__, \\____/ \\___/ \\__|\n" + "                          __/ |                \n" + "                         |___/                 \n" + "\n" + "            https://github.com/KittyBot-Org/KittyBot" + "\n");
        LOG.info("Starting...");

        Config.load("config.json");

        try {
            lavalink = new JdaLavalink(Config.BOT_ID, 1, this::getShardById);
            for (LavalinkNode node : Config.LAVALINK_NODES) {
                lavalink.addNode(new URI("ws://" + node.host + ":" + node.port), node.password);
            }

            AUDIO_PLAYER_MANAGER.registerSourceManager(new YoutubeAudioSourceManager());
            AUDIO_PLAYER_MANAGER.registerSourceManager(new BandcampAudioSourceManager());
            AUDIO_PLAYER_MANAGER.registerSourceManager(new VimeoAudioSourceManager());
            AUDIO_PLAYER_MANAGER.registerSourceManager(new TwitchStreamAudioSourceManager());
            AUDIO_PLAYER_MANAGER.registerSourceManager(new HttpAudioSourceManager());
            AudioSourceManagers.registerRemoteSources(AUDIO_PLAYER_MANAGER);

            CommandManager.registerCommands();

            jda = JDABuilder.create(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_VOICE_STATES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_INVITES,

                    GatewayIntent.DIRECT_MESSAGES, GatewayIntent.DIRECT_MESSAGE_REACTIONS)
                    .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setToken(Config.BOT_TOKEN)
                    .addEventListeners(new OnGuildEvent(), new OnGuildMemberEvent(), new OnEmoteEvent(), new OnGuildMessageEvent(), new OnGuildVoiceEvent(), new OnGuildReadyEvent(), new OnReadyEvent(), new OnInviteEvent(), lavalink, new Paginator())
                    .setVoiceDispatchInterceptor(lavalink.getVoiceInterceptor())
                    .setActivity(Activity.playing("loading..."))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGatewayEncoding(GatewayEncoding.ETF)
                    .build()
                    .awaitReady();

            RestAction.setDefaultFailure(null);

            if (Config.isSet(Config.DISCORD_BOT_LIST_TOKEN)) {
                discordBotListAPI = new DiscordBotListAPI.Builder().token(Config.DISCORD_BOT_LIST_TOKEN).botId(Config.BOT_ID).build();
            }

            SCHEDULER.scheduleAtFixedRate(() -> MessageCache.getCache().entrySet().removeIf(entry -> entry.getValue().getCreation().isBefore(OffsetDateTime.now().minusMinutes(10).toInstant())), 1, 1, TimeUnit.HOURS);

            Database.init(jda);

            new WebService(6969);

            jda.getPresence().setStatus(OnlineStatus.ONLINE);
            jda.getPresence().setActivity(Activity.watching("you \uD83D\uDC40"));
            if (Config.isSet(Config.LOG_CHANNEL_ID)) {
                sendToPublicLogChannel("I'm now online uwu");
            }
        } catch (Exception e) {
            LOG.error("Error while initializing JDA", e);
            close();
        }
    }

    public static void sendToPublicLogChannel(String description) {
        var guild = jda.getGuildById(Config.SUPPORT_GUILD_ID);
        if (guild == null) {
            return;
        }
        var channel = guild.getTextChannelById(Config.LOG_CHANNEL_ID);
        if (channel != null) {
            channel.sendMessage(new EmbedBuilder().setTitle("Log")
                    .setDescription(description)
                    .setColor(new Color(76, 80, 193))
                    .setFooter(jda.getSelfUser().getName(), jda.getSelfUser().getAvatarUrl())
                    .setTimestamp(Instant.now())
                    .build()).queue();
        }
    }

    public static void main(String[] args) {
        new KittyBot();
    }

    public static AudioPlayerManager getAudioPlayerManager() {
        return AUDIO_PLAYER_MANAGER;
    }

    public static JdaLavalink getLavalink() {
        return lavalink;
    }

    public static OkHttpClient getHttpClient() {
        return HTTP_CLIENT;
    }

    public static JDA getJda() {
        return jda;
    }

    public static DiscordBotListAPI getDiscordBotListAPI() {
        return discordBotListAPI;
    }

    public static ScheduledExecutorService getScheduler() {
        return SCHEDULER;
    }

    public static EventWaiter getWaiter() {
        return WAITER;
    }

    private JDA getShardById(int id) {
        return jda;
    }

    public void close() {
        lavalink.getLinks().forEach(Link::destroy);
        jda.shutdown();
        SQL.close();
        System.exit(0);
    }

}
