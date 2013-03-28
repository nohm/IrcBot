package org.snack.irc.settings;

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
		jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config.txt"));

		initSettings();
		initSpeech();
	}

	// All the settings
	public static HashMap<String, String> sett_str;
	public static HashMap<String, Boolean> sett_bool;
	public static HashMap<String, Integer> sett_int;
	public static HashMap<String, Chan> channels;

	/**
	 * Initialize the settings
	 */
	private static void initSettings() {
		sett_str = new HashMap<String, String>();
		sett_bool = new HashMap<String, Boolean>();
		sett_int = new HashMap<String, Integer>();
		channels = new HashMap<String, Chan>();

		sett_str.put("ADMIN", jo.getString("admin"));
		sett_bool.put("DEBUG", jo.getBoolean("debug"));
		sett_bool.put("INTERFACE", jo.getBoolean("interface"));
		sett_int.put("SCROLLBACK", jo.getInt("scrollback"));

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
			JSONObject func = obj.getJSONObject("functions");
			JSONObject greet = func.getJSONObject("greet");
			JSONObject joResultBots = obj.getJSONObject("bots");
			ArrayList<Bot> bots = new ArrayList<Bot>();
			for (int j = 1; j <= joResultBots.size(); j++) {
				JSONObject bot = joResultBots.getJSONObject("bot" + j);
				bots.add(new Bot(bot.getString("name"), bot.getBoolean("greet"), bot.getBoolean("html"), bot.getBoolean("lastfm"), bot.getBoolean("weather"), bot
						.getBoolean("quote"), bot.getBoolean("tell"), bot.getBoolean("translate"), bot.getBoolean("romaji")));
			}
			Chan chan = new Chan(obj.getString("name"), greet.getBoolean("enabled"), greet.getString("visible"), func.getBoolean("html"), func.getBoolean("lastfm"),
					func.getBoolean("weather"), func.getBoolean("quote"), func.getBoolean("tell"), func.getBoolean("translate"), func.getBoolean("romaji"), bots);
			channels.put(chan.name, chan);
		}

		JSONObject joResultSettings = jo.getJSONObject("settings");
		sett_str.put("SAVE_LOC", joResultSettings.getString("config"));
		sett_str.put("IDENTIFIERS", joResultSettings.getString("identifiers"));
		JSONObject joQuoteSettings = joResultSettings.getJSONObject("quote");
		sett_int.put("QUOTE_MIN", joQuoteSettings.getInt("min"));
		sett_int.put("QUOTE_MAX", joQuoteSettings.getInt("max"));
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

		speech.put("GREET", joSpeech.getString("greet"));

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
		speech.put("QU_ADD", joQuote.getString("add"));

		JSONObject joTell = joSpeech.getJSONObject("tell");
		speech.put("TE_ADD", joTell.getString("add"));
		speech.put("TE_TEL", joTell.getString("tell"));
		speech.put("TE_ERR", joTell.getString("error"));

		JSONObject joTranslate = joSpeech.getJSONObject("translate");
		speech.put("TR_SUC", joTranslate.getString("success"));
		speech.put("TR_ERR", joTranslate.getString("error"));

		JSONObject joRomaji = joSpeech.getJSONObject("romaji");
		speech.put("RK_ROM", joRomaji.getString("romaji"));
		speech.put("RK_KAT", joRomaji.getString("katakana"));
	}
}
