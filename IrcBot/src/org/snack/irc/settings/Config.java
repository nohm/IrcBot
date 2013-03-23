package org.snack.irc.settings;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;

public class Config {
	private static Config c;
	private static JSONObject jo;

	/**
	 * Make sure it only gets read once, reads a config file and stores the data
	 */
	public static void initialize() throws Exception {
		if (c == null) {
			c = new Config();
		}
		String path = Config.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String decodedPath = URLDecoder.decode(path, "UTF-8");
		System.out.println(decodedPath);
		String[] jsonArray = SettingParser.parseTxt(decodedPath + "config.txt");
		String json = "";
		for (String j : jsonArray) {
			json += j;
		}
		jo = (JSONObject) JSONSerializer.toJSON(json);

		initSettings();
		initSpeech();
	}

	// All the settings
	public static HashMap<String, String> sett_str;
	public static HashMap<String, Boolean> sett_bool;
	public static HashMap<String, Chan> channels;
	public static ArrayList<Bot> bots;

	/**
	 * Initialize the settings
	 */
	private static void initSettings() {
		sett_str = new HashMap<String, String>();
		sett_bool = new HashMap<String, Boolean>();
		channels = new HashMap<String, Chan>();
		bots = new ArrayList<Bot>();

		sett_str.put("ADMIN", jo.getString("admin"));
		sett_bool.put("DEBUG", jo.getBoolean("debug"));

		JSONObject joResultBot = jo.getJSONObject("bot");
		sett_str.put("BOT_NAME", joResultBot.getString("name"));
		sett_str.put("BOT_ALT_NAME", joResultBot.getString("alt_name"));
		sett_str.put("BOT_LOGIN", joResultBot.getString("login"));
		sett_str.put("BOT_VERSION", joResultBot.getString("version"));
		sett_str.put("BOT_PASS", joResultBot.getString("password"));

		sett_str.put("SERVER", jo.getString("server"));

		JSONObject joResultChannels = jo.getJSONObject("channels");
		for (int i = 1; i <= joResultChannels.size(); i++) {
			JSONObject obj = joResultChannels.getJSONObject("channel" + i);
			Chan chan = new Chan(obj.getString("name"), obj.getBoolean("html"), obj.getBoolean("lastfm"), obj.getBoolean("weather"), obj.getBoolean("quote"),
					obj.getBoolean("tell"));
			channels.put(chan.getName(), chan);
		}

		JSONObject joResultSettings = jo.getJSONObject("settings");
		sett_str.put("SAVE_LOC", joResultSettings.getString("config"));

		JSONObject joResultBots = jo.getJSONObject("disable");
		for (int i = 1; i <= joResultBots.size(); i++) {
			JSONObject bot = joResultBots.getJSONObject("bot" + i);
			bots.add(new Bot(bot.getString("name"), bot.getBoolean("html"), bot.getBoolean("lastfm"), bot.getBoolean("weather"), bot.getBoolean("quote"), bot.getBoolean("tell")));
		}
	}

	// All the speech lines
	public static HashMap<String, String> speech;

	/**
	 * Initialize the speech lines
	 */
	private static void initSpeech() {
		speech = new HashMap<String, String>();

		JSONObject joSpeech = jo.getJSONObject("speech");

		JSONObject joMute = joSpeech.getJSONObject("mute");
		speech.put("MUTE", joMute.getString("mute"));
		speech.put("UNMUTE", joMute.getString("unmute"));

		JSONObject joWeather = joSpeech.getJSONObject("weather");
		speech.put("WE_SUC", joWeather.getString("success"));
		speech.put("WE_ERR", joWeather.getString("error"));

		JSONObject joLastfm = joSpeech.getJSONObject("lastfm");
		JSONObject joLastfmSuccess = joLastfm.getJSONObject("success");
		speech.put("LA_SUC_NP", joLastfmSuccess.getString("now_playing"));
		speech.put("LA_SUC_LP", joLastfmSuccess.getString("last_played"));
		speech.put("LA_SUC_ART", joLastfmSuccess.getString("no_artist"));
		speech.put("LA_SUC_ALB", joLastfmSuccess.getString("no_album"));
		speech.put("LA_SUC SON", joLastfmSuccess.getString("no_song"));
		speech.put("LA_ERR", joLastfm.getString("error"));

		JSONObject joHtml = joSpeech.getJSONObject("html");
		JSONObject joHtmlSuccess = joHtml.getJSONObject("success");
		JSONObject joHtmlSuccessFile = joHtmlSuccess.getJSONObject("file");
		JSONObject joHtmlError = joHtml.getJSONObject("error");
		speech.put("HT_TIT", joHtmlSuccess.getString("title"));
		speech.put("HT_SUC_SIZE0", joHtmlSuccessFile.getString("size_zero"));
		speech.put("HT_SUC_!SIZE0", joHtmlSuccessFile.getString("size_not_zero"));
		speech.put("HT_SUC_ENC", joHtmlSuccessFile.getString("encoding"));
		speech.put("HT_SUC_!ENC", joHtmlSuccessFile.getString("no_encoding"));
		speech.put("HT_SUC", joHtmlSuccessFile.getString("file_full"));
		speech.put("HT_ERR_403", joHtmlError.getString("403"));
		speech.put("HT_ERR_404", joHtmlError.getString("404"));
		speech.put("HT_ERR_?", joHtmlError.getString("?"));

		JSONObject joQuote = joSpeech.getJSONObject("quote");
		speech.put("QU_SUC", joQuote.getString("success"));
		speech.put("QU_ERR", joQuote.getString("error"));

		JSONObject joTell = joSpeech.getJSONObject("tell");
		speech.put("TE_ADD", joTell.getString("add"));
		speech.put("TE_TEL", joTell.getString("tell"));
	}
}
