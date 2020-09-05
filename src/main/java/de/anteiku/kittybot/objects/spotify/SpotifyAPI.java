package de.anteiku.kittybot.objects.spotify;

import com.wrapper.spotify.SpotifyApi;
import com.wrapper.spotify.requests.authorization.client_credentials.ClientCredentialsRequest;
import de.anteiku.kittybot.KittyBot;
import de.anteiku.kittybot.objects.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class SpotifyAPI {
    private static final SpotifyApi SPOTIFY_API = new SpotifyApi.Builder().setClientId(Config.SPOTIFY_CLIENT_ID).setClientSecret(Config.SPOTIFY_CLIENT_SECRET).build();
    private static final ClientCredentialsRequest CLIENT_CREDENTIALS_REQUEST = SPOTIFY_API.clientCredentials().build();

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAPI.class);

    private static int hits;

    public static void initialize(){
        KittyBot.getScheduler().scheduleAtFixedRate(SpotifyAPI::refreshAccessToken, 0, 1, TimeUnit.HOURS);
    }

    private static void refreshAccessToken(){
        try{
            SPOTIFY_API.setAccessToken(CLIENT_CREDENTIALS_REQUEST.execute().getAccessToken());
            hits = 0;
        }
        catch (Exception ex){
            var scheduler = KittyBot.getScheduler();
            hits++;
            if (hits < 10){
                LOGGER.warn("Updating the access token failed. Retrying in 10 seconds", ex);
                scheduler.schedule(SpotifyAPI::refreshAccessToken, 10, TimeUnit.SECONDS);
            }
            else{
                LOGGER.error("Updating the access token failed. Retrying in 20 seconds", ex);
                scheduler.schedule(SpotifyAPI::refreshAccessToken, 20, TimeUnit.SECONDS);
            }
        }
    }

    public static SpotifyApi getAPI(){
        return SPOTIFY_API;
    }
}