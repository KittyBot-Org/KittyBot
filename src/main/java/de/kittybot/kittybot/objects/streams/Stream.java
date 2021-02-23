package de.kittybot.kittybot.objects.streams;

import de.kittybot.kittybot.objects.enums.Language;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.time.Instant;
import java.util.Arrays;


public class Stream{

	private final String userName, streamTitle, thumbnailUrl;
	private final long streamId, userId;
	private final int viewerCount;
	private final Instant startedAt;
	private final Language language;
	private final StreamType type;
	private Game game;

	public Stream(long streamId, String streamTitle, String thumbnailUrl, long userId, String userName, Game game, int viewerCount, Instant startedAt, Language language, StreamType type){
		this.streamId = streamId;
		this.streamTitle = streamTitle;
		this.thumbnailUrl = thumbnailUrl;
		this.userId = userId;
		this.userName = userName;
		this.game = game;
		this.viewerCount = viewerCount;
		this.startedAt = startedAt;
		this.language = language;
		this.type = type;
	}

	public static Stream fromTwitchJSON(DataObject json){
		var lang = Arrays.stream(Language.values()).filter(l -> l.getShortname().equalsIgnoreCase(json.getString("language"))).findFirst();
		return new Stream(
			json.getLong("id"),
			json.getString("title"),
			json.getString("thumbnail_url"),
			json.getLong("user_id"),
			json.getString("user_name"),
			new Game(json.getInt("game_id"), json.getString("game_name")),
			json.getInt("viewer_count"),
			Instant.parse(json.getString("started_at")),
			lang.orElse(Language.UNKNOWN),
			StreamType.TWITCH
		);
	}

	public String getUserName(){
		return this.userName;
	}

	public String getStreamTitle(){
		return this.streamTitle;
	}

	public String getThumbnailUrl(int width, int height){
		return this.thumbnailUrl.replace("{width}", String.valueOf(width)).replace("{height}", String.valueOf(height)) + "?v=" + System.currentTimeMillis();
	}

	public long getStreamId(){
		return this.streamId;
	}

	public long getUserId(){
		return this.userId;
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

	public StreamType getType(){
		return this.type;
	}

	public String getStreamUrl(){
		if(this.type == StreamType.TWITCH){
			return this.type.getBaseUrl() + this.userName;
		}
		if(this.type == StreamType.YOUTUBE){
			return this.type.getBaseUrl() + "c/" + this.userName;
		}
		return null;
	}

}
