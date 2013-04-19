package irc.model;

import irc.settings.Config;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Stores all the channel settings, also keeps defaults
 * 
 * @author snack
 * 
 */
public class Chan {
	// Name
	public final String name;
	public boolean join;
	public boolean mute;
	public final HashMap<String, Boolean> functions;
	public final HashMap<String, Boolean> def_func;
	public final HashMap<String, String> defaults;
	public final HashMap<String, Boolean> ignore;
	public final ArrayList<Bot> bots;

	public Chan(String name, boolean join, boolean mute, ArrayList<Bot> bots) {
		this.name = name;
		this.join = join;
		this.mute = mute;
		this.bots = bots;
		functions = new HashMap<String, Boolean>();
		def_func = new HashMap<String, Boolean>();
		defaults = new HashMap<String, String>();
		ignore = new HashMap<String, Boolean>();
	}

	public void putFunction(String key, Boolean value) {
		functions.put(key, value);
		def_func.put(key, value);
	}

	public void putDefault(String key, String value) {
		defaults.put(key, value);
	}

	public void putIgnore(String key, Boolean value) {
		ignore.put(key, value);
	}

	public void initDefault() {
		try {
			functions.putAll(Config.getDefaultBooleans());
			def_func.putAll(functions);
			defaults.putAll(Config.getDefaultChannelStrings());
			ignore.putAll(Config.getDefaultIgnore());
		} catch (Exception e) {
		}
	}
}