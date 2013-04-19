package irc.model;

import com.mongodb.BasicDBObject;

public class Quote extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String CHANNEL_KEY = "Channel";
	public static final String NAME_KEY = "Name";
	public static final String MESSAGE_KEY = "Message";

	public Quote(String channel, String name, String message) {
		super(3);
		put(CHANNEL_KEY, channel);
		put(NAME_KEY, name);
		put(MESSAGE_KEY, message);
	}

	public String getChannel() {
		return (String) get(CHANNEL_KEY);
	}

	public String getName() {
		return (String) get(NAME_KEY);
	}

	public String getMessage() {
		return (String) get(MESSAGE_KEY);
	}
}
