package org.snack.irc.settings;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;

public class Configuration {
	private static Configuration c;

	/**
	 * Make sure it only gets read once
	 */
	public static void initialize() {
		if (c == null) {
			c = new Configuration();
		}
	}

	// All the settings
	public static boolean DEBUG;
	public static String BOT_NAME;
	public static String BOT_ALT_NAME;
	public static String BOT_LOGIN;
	public static String BOT_VERSION;
	public static String BOT_PASS;
	public static String SERVER;
	public static Map<String, Chan> CHANNELS;
	public static String SAVE_LOC;
	public static ArrayList<Bot> BOTS;

	/**
	 * Reads a config file and stores the data
	 */
	private Configuration() {
		try {
			String path = Configuration.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			System.out.println(decodedPath);
			String[] jsonArray = SettingParser.parseTxt(decodedPath + "config.txt");

			String json = "";
			for (String j : jsonArray) {
				json += j;
			}

			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);

			// Debug
			DEBUG = Boolean.valueOf(jo.getString("debug"));

			// Bot settings
			JSONObject joResultBot = (JSONObject) jo.get("bot");
			BOT_NAME = joResultBot.getString("name");
			BOT_ALT_NAME = joResultBot.getString("alt_name");
			BOT_LOGIN = joResultBot.getString("login");
			BOT_VERSION = joResultBot.getString("version");
			BOT_PASS = joResultBot.getString("password");

			// Server
			SERVER = jo.getString("server");

			// Channels
			CHANNELS = new HashMap<String, Chan>();
			JSONObject joResultChannels = (JSONObject) jo.get("channels");
			for (int i = 1; i <= joResultChannels.size(); i++) {
				JSONObject obj = (JSONObject) joResultChannels.get("channel" + i);
				Chan chan = new Chan(obj.getString("name"), obj.getBoolean("html"), obj.getBoolean("lastfm"), obj.getBoolean("weather"), obj.getBoolean("quote"),
						obj.getBoolean("tell"));
				CHANNELS.put(chan.getName(), chan);
			}

			// Save location
			JSONObject joResultSettings = (JSONObject) jo.get("settings");
			SAVE_LOC = joResultSettings.getString("config");

			// Bots
			BOTS = new ArrayList<Bot>();
			JSONObject joResultBots = (JSONObject) jo.get("disable");
			for (int i = 1; i <= joResultBots.size(); i++) {
				JSONObject bot = (JSONObject) joResultBots.get("bot" + i);
				BOTS.add(new Bot(bot.getString("name"), bot.getBoolean("html"), bot.getBoolean("lastfm"), bot.getBoolean("weather"), bot.getBoolean("quote"), bot
						.getBoolean("tell")));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error in the config file.");
			System.exit(-1);
		}
	}
}
