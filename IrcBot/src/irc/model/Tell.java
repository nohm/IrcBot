package irc.model;

import com.mongodb.BasicDBObject;

public class Tell extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String SENDER_KEY = "Sender";
	public static final String NAME_KEY = "Name";
	public static final String MESSAGE_KEY = "Message";

	public Tell(String sender, String name, String message) {
		super(3);
		put(SENDER_KEY, sender.toLowerCase());
		put(NAME_KEY, name);
		put(MESSAGE_KEY, message);
	}

	public String getSender() {
		return (String) get(SENDER_KEY);
	}

	public String getName() {
		return (String) get(NAME_KEY);
	}

	public String getMessage() {
		return (String) get(MESSAGE_KEY);
	}
}
