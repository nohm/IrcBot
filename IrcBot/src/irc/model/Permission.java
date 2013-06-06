package irc.model;

import com.mongodb.BasicDBObject;

public class Permission extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String NAME_KEY = "Name";
	public static final String CHANNEL_KEY = "Channel";
	public static final String PERMISSION_KEY = "Permission";

	public Permission(String name, String channel, String permission) {
		super(3);
		put(NAME_KEY, name);
		put(CHANNEL_KEY, channel);
		put(PERMISSION_KEY, permission);
	}

	public String getName() {
		return getString(NAME_KEY);
	}

	public String getChannel() {
		return getString(CHANNEL_KEY);
	}

	public String getPermission() {
		return getString(PERMISSION_KEY);
	}
}
