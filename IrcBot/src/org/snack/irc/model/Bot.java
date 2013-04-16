package org.snack.irc.model;

import java.util.HashMap;

import org.snack.irc.settings.Config;

/**
 * Stores all the bot settings this bot needs to keep track of
 * 
 * @author snack
 * 
 */
public class Bot {
	public final String name;
	public boolean enabled;
	public final HashMap<String, Boolean> functions;

	public Bot(String name, boolean enabled) {
		this.name = name;
		this.enabled = enabled;
		functions = new HashMap<String, Boolean>();
	}

	public void putFunction(String key, boolean value) {
		functions.put(key, value);
	}

	public void initDefault() {
		try {
			functions.putAll(Config.getDefaultBooleans());
		} catch (Exception e) {
		}
	}
}
