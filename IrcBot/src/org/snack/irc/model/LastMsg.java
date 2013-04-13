package org.snack.irc.model;

import com.mongodb.BasicDBObject;

public class LastMsg extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String NAME_KEY = "Name";
	public static final String TIME_KEY = "Time";

	public LastMsg(String name, long time) {
		super(2);
		put(NAME_KEY, name);
		put(TIME_KEY, time);
	}

	public String getName() {
		return (String) get(NAME_KEY);
	}

	public long getTime() {
		return getLong(TIME_KEY);
	}
}
