package org.snack.irc.lastfm;

/**
 * Simply represents someone that stored an last.fm username
 * @author snack
 *
 */
public class LastfmUser {
	
	private String name;
	private String username;

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
