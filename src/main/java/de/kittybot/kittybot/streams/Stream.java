package de.kittybot.kittybot.streams;

import de.kittybot.kittybot.objects.Language;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.time.Instant;
import java.util.Arrays;


public class Stream{

	private final String userName, streamTitle, thumbnailUrl;
	private final long streamId, userId;
	private final int gameId;
	private final int viewerCount;
	private final Instant startedAt;
	private final Language language;
	private Game game;

	public Stream(long streamId, String streamTitle, String thumbnailUrl, long userId, String userName, int gameId, Game game, int viewerCount, Instant startedAt, Language language){
		this.streamId = streamId;
		this.streamTitle = streamTitle;
		this.thumbnailUrl = thumbnailUrl;
		this.userId = userId;
		this.userName = userName;
		this.gameId = gameId;
		this.game = game;
		this.viewerCount = viewerCount;
		this.startedAt = startedAt;
		this.language = language;
	}

	public static Stream fromTwitchJSON(DataObject json){
		return new Stream(
				json.getLong("id"),
				json.getString("title"),
				json.getString("thumbnail_url"),
				json.getLong("user_id"),
				json.getString("user_name"),
				json.getInt("game_id"),
				new Game(json.getInt("game_id"), json.getString("game_name"), ""),
				json.getInt("viewer_count"),
				Instant.parse(json.getString("started_at")),
				Arrays.stream(Language.values()).filter(l -> l.getShortname().equalsIgnoreCase(json.getString("language"))).findFirst().get()
		);
	}

	public String getUserName(){
		return this.userName;
	}

	public String getStreamTitle(){
		return this.streamTitle;
	}

	public String getThumbnailUrl(){
		return this.thumbnailUrl;
	}

	public long getStreamId(){
		return this.streamId;
	}

	public long getUserId(){
		return this.userId;
	}

	public int getGameId(){
		return this.gameId;
	}

	public Game getGame(){
		return this.game;
	}

	public void setGame(Game game){
		this.game = game;
	}

	public int getViewerCount(){
		return this.viewerCount;
	}

	public Instant getStartedAt(){
		return this.startedAt;
	}

	public Language getLanguage(){
		return this.language;
	}

}
