package org.snack.irc.model;

import java.util.HashMap;

public class HelpModule {

	public final String name;
	public final String permission;
	public final HashMap<String, String> modules;

	public HelpModule(String name, String permission) {
		this.name = name;
		this.permission = permission;
		modules = new HashMap<String, String>();
	}

	public void putModule(String key, String value) {
		modules.put(key, value);
	}

	public boolean hasPermission(String permission) {
		if (this.permission.equals(permission)) {
			return true;
		} else if (this.permission.equals("all")) {
			return true;
		} else if (this.permission.equals("main") && permission.equals("global")) {
			return true;
		} else {
			return false;
		}
	}

	public String toString(Chan chan) {
		String response = name + " (" + permission + "): ";
		for (String module : modules.keySet()) {
			try {
				if (chan.functions.get(module)) {
					response += module + ", ";
				}
			} catch (Exception e) {
				response += module + ", ";
			}
		}
		response = response.substring(0, response.length() - 2);
		return response;
	}
}