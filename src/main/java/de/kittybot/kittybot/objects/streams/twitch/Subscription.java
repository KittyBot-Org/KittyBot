package de.kittybot.kittybot.objects.streams.twitch;

import net.dv8tion.jda.api.utils.data.DataObject;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public class Subscription{

	private final String id;
	private Status status;
	private final Type type;
	private final int version;
	private final Set<Condition> conditions;
	private final Instant createdAt;
	private final Transport transport;

	public Subscription(String id, Status status, Type type, int version, Set<Condition> conditions, Instant createdAt, Transport transport){
		this.id = id;
		this.status = status;
		this.type = type;
		this.version = version;
		this.conditions = conditions;
		this.createdAt = createdAt;
		this.transport = transport;
	}

	public String getId(){
		return this.id;
	}

	public Status getStatus(){
		return this.status;
	}

	public void setStatus(Status status){
		this.status = status;
	}

	public Type getType(){
		return this.type;
	}

	public int getVersion(){
		return this.version;
	}

	public Set<Condition> getConditions(){
		return this.conditions;
	}

	public Instant getCreatedAt(){
		return this.createdAt;
	}

	public Transport getTransport(){
		return this.transport;
	}

	public static Subscription fromJSON(DataObject json){
		return new Subscription(
			json.getString("id"),
			Status.fromText(json.getString("status")),
			Type.fromText(json.getString("type")),
			json.getInt("version"),
			Condition.fromJSON(json.getObject("condition")),
			Instant.parse(json.getString("created_at")),
			Transport.fromJSON(json.getObject("transport"))
		);
	}

	public enum Status{

		ENABLED("enabled"),
		WEBHOOK_CALLBACK_VERIFICATION_PENDING("webhook_callback_verification_pending"),
		WEBHOOK_CALLBACK_VERIFICATION_FAILED("webhook_callback_verification_failed"),
		NOTIFICATION_FAILURES_EXCEEDED("notification_failures_exceeded"),
		AUTHORIZATION_REVOKED("authorization_revoked"),
		USER_REMOVED("user_removed");

		private final String text;

		Status(String text){
			this.text = text;
		}

		public static Status fromText(String text){
			for(var value : values()){
				if(value.text.equals(text)){
					return value;
				}
			}
			throw new IllegalArgumentException("unknown status received");
		}

		public String getText(){
			return this.text;
		}

	}

	public enum Type{

		STREAM_ONLINE("stream.online"),
		STREAM_OFFLINE("stream.offline");

		private final String text;

		Type(String text){
			this.text = text;
		}

		public static Type fromText(String text){
			for(var value : values()){
				if(value.text.equals(text)){
					return value;
				}
			}
			throw new IllegalArgumentException("unknown type received");
		}

		public String getText(){
			return this.text;
		}

	}

	public static class Condition{

		private final String key;
		private final Object value;

		public Condition(String key, Object value){
			this.key = key;
			this.value = value;
		}

		public static Set<Condition> fromJSON(DataObject json){
			var conditions = json.keys();
			return conditions.stream().map(key -> new Condition(key, json.getString(key))).collect(Collectors.toSet());
		}

		public  DataObject toJSON(){
			return DataObject.empty()
				.put(this.key, this.value);
		}

		public String getKey(){
			return this.key;
		}

		public Object getValue(){
			return this.value;
		}

	}

	public static class Transport{

		private final String method, callback;

		public Transport(String method, String callback){
			this.method = method;
			this.callback = callback;
		}

		public static Transport fromJSON(DataObject json){
			return new Transport(json.getString("method"), json.getString("callback"));
		}

		public String getMethod(){
			return this.method;
		}

		public String getCallback(){
			return this.callback;
		}

	}

}
