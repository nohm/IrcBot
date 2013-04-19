package irc.model;

import com.mongodb.BasicDBObject;

public class LastMsg extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String NAME_KEY = "Name";
	public static final String TIME_KEY = "Time";
	public static final String TEXT_KEY = "Text";

	public LastMsg(String name, long time, String msg) {
		super(2);
		put(NAME_KEY, name);
		put(TIME_KEY, time);
		put(TEXT_KEY, msg);
	}

	public String getName() {
		return getString(NAME_KEY);
	}

	public long getTime() {
		return getLong(TIME_KEY);
	}

	public String getText() {
		return getString(TEXT_KEY);
	}
}
