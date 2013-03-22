package org.snack.irc.model;

/**
 * Simply represents someone that stored an last.fm username
 * 
 * @author snack
 * 
 */
public class LastfmUser {

	private final String name;
	private final String username;

	public LastfmUser(String name, String username) {
		this.name = name;
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public String getUsername() {
		return username;
	}
}
