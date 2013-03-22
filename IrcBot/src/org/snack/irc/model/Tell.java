package org.snack.irc.model;

/**
 * Stores the name, sender and message for each tell case.
 * 
 * @author snack
 * 
 */
public class Tell {

	private final String name;
	private final String sender;
	private final String message;

	public Tell(String name, String sender, String message) {
		this.name = name;
		this.sender = sender;
		this.message = message;
	}

	public String getName() {
		return name;
	}

	public String getSender() {
		return sender;
	}

	public String getMessage() {
		return message;
	}
}
