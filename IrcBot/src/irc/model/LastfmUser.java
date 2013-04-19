package irc.model;

import com.mongodb.BasicDBObject;

public class LastfmUser extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String NAME_KEY = "Name";
	public static final String USERNAME_KEY = "Username";

	public LastfmUser(String name, String username) {
		super(2);
		put(NAME_KEY, name);
		put(USERNAME_KEY, username);
	}

	public String getName() {
		return (String) get(NAME_KEY);
	}

	public String getUsername() {
		return (String) get(USERNAME_KEY);
	}
}
