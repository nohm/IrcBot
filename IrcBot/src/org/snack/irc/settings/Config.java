package org.snack.irc.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.model.HelpModule;

public class Config {
	private static Config c;

	/**
	 * Make sure it only gets read once, reads a config file and stores the data
	 */
	public static void initialize() throws Exception {
		if (c == null) {
			c = new Config();
		}

		admins = new HashMap<String, String>();
		sett_str = new HashMap<String, String>();
		sett_bool = new HashMap<String, Boolean>();
		sett_int = new HashMap<String, Integer>();
		channels = new HashMap<String, Chan>();
		speech = new HashMap<String, String>();
		help = new HashMap<String, HelpModule>();

		initAdmins();
		initMainSettings();
		initChannels();
		initSpeech();
		initHelp();
	}

	// All the settings
	public static HashMap<String, String> admins;
	public static HashMap<String, String> sett_str;
	public static HashMap<String, Boolean> sett_bool;
	public static HashMap<String, Integer> sett_int;
	public static HashMap<String, Chan> channels;
	public static HashMap<String, String> speech;
	public static HashMap<String, HelpModule> help;

	private static void initAdmins() throws Exception {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config/admins.txt"));
		JSONArray admin = jo.getJSONArray("admins");
		for (int i = 0; i < admin.size(); i++) {
			admins.put(admin.getJSONObject(i).getString("name"), admin.getJSONObject(i).getString("type"));
		}
	}

	private static void initMainSettings() throws Exception {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config/settings.txt"));
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

		JSONObject joResultSettings = jo.getJSONObject("settings");
		sett_str.put("LOG_LOC", joResultSettings.getString("log"));
		sett_str.put("IDENTIFIERS", joResultSettings.getString("identifiers"));
		JSONObject joQuoteSettings = joResultSettings.getJSONObject("quote");
		sett_int.put("QUOTE_MIN", joQuoteSettings.getInt("min"));
		sett_int.put("QUOTE_MAX", joQuoteSettings.getInt("max"));
	}

	private static void initChannels() throws Exception {
		for (File folder : new File("config").listFiles()) {
			if (folder.isDirectory() && folder.getName().startsWith("#")) {
				initChannel(folder.getAbsolutePath(), folder.getName());
			}
		}
	}

	private static void initChannel(String path, String name) throws Exception {
		ArrayList<Bot> bots = new ArrayList<Bot>();
		for (File bot : new File(path).listFiles()) {
			if (!bot.isDirectory() && bot.getName().startsWith("bot-")) {
				JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt(bot.getAbsolutePath()));
				JSONArray func = jo.getJSONArray("functions");
				Bot b = new Bot(jo.getString("name"));
				b.initDefault();
				for (int i = 0; i < func.size(); i++) {
					JSONObject function = func.getJSONObject(i);
					String cleaned = function.toString().replaceAll("[{}\"]", "");
					b.functions.put(cleaned.substring(0, cleaned.indexOf(":")), Boolean.valueOf(cleaned.substring(cleaned.indexOf(":") + 1)));
				}
				bots.add(b);
			}
		}
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt(path + "/functions.txt"));
		JSONArray func = jo.getJSONArray("functions");
		Chan chan = new Chan(name, jo.getBoolean("join"), jo.getBoolean("mute"), bots);
		chan.initDefault();
		for (int i = 0; i < func.size(); i++) {
			JSONObject function = func.getJSONObject(i);
			String cleaned = function.toString().replaceAll("[{}\"]", "");
			chan.putFunction(cleaned.substring(0, cleaned.indexOf(":")), Boolean.valueOf(cleaned.substring(cleaned.indexOf(":") + 1)));
		}
		JSONArray def = jo.getJSONArray("defaults");
		for (int i = 0; i < def.size(); i++) {
			JSONObject function = def.getJSONObject(i);
			String cleaned = function.toString().replaceAll("[{}\"]", "");
			chan.putDefault(cleaned.substring(0, cleaned.indexOf(":")), cleaned.substring(cleaned.indexOf(":") + 1));
		}
		channels.put(chan.name, chan);
	}

	private static void initSpeech() throws Exception {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config/speech.txt"));

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

		JSONObject joWiki = joSpeech.getJSONObject("wiki");
		speech.put("WI_SUC", joWiki.getString("success"));
		speech.put("WI_NO", joWiki.getString("nothing"));
		speech.put("WI_ERR", joWiki.getString("error"));

		JSONObject joSearch = joSpeech.getJSONObject("search");
		speech.put("SE_SUC", joSearch.getString("success"));
		speech.put("SE_ERR", joSearch.getString("error"));

		JSONObject joDefine = joSpeech.getJSONObject("define");
		speech.put("DE_SUC", joDefine.getString("success"));
		speech.put("DE_ERR", joDefine.getString("error"));

		JSONObject joBooru = joSpeech.getJSONObject("booru");
		speech.put("BO_SUC", joBooru.getString("success"));
		speech.put("BO_ERR", joBooru.getString("error"));
	}

	private static void initHelp() throws Exception {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config/help.txt"));

		speech.put("modules", jo.getString("modules"));
		speech.put("unknown_module", jo.getString("unknown_module"));

		JSONArray groups = jo.getJSONArray("groups");
		for (int i = 0; i < groups.size(); i++) {
			JSONObject group = groups.getJSONObject(i);
			HelpModule module = new HelpModule(group.getString("name"), group.getString("permission"));
			JSONArray modules = group.getJSONArray("modules");
			for (int j = 0; j < modules.size(); j++) {
				JSONObject item = modules.getJSONObject(j);
				String cleaned = item.toString().replaceAll("[{}\"]", "");
				module.putModule(cleaned.substring(0, cleaned.indexOf(":")), cleaned.substring(cleaned.indexOf(":") + 1));
			}
			help.put(module.name, module);
		}
	}

	public static HashMap<String, String> getDefaultChannelStrings() throws Exception {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config/default_config.txt"));

		HashMap<String, String> defaults = new HashMap<String, String>();

		JSONArray def = jo.getJSONArray("defaults");
		for (int i = 0; i < def.size(); i++) {
			JSONObject function = def.getJSONObject(i);
			String cleaned = function.toString().replaceAll("[{}\"]", "");
			defaults.put(cleaned.substring(0, cleaned.indexOf(":")), cleaned.substring(cleaned.indexOf(":") + 1));
		}

		return defaults;
	}

	public static HashMap<String, Boolean> getDefaultBooleans() throws Exception {
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(TxtReader.parseTxt("config/default_config.txt"));

		HashMap<String, Boolean> defaults = new HashMap<String, Boolean>();

		JSONArray def = jo.getJSONArray("functions");
		for (int i = 0; i < def.size(); i++) {
			JSONObject function = def.getJSONObject(i);
			String cleaned = function.toString().replaceAll("[{}\"]", "");
			defaults.put(cleaned.substring(0, cleaned.indexOf(":")), Boolean.valueOf(cleaned.substring(cleaned.indexOf(":") + 1)));
		}

		return defaults;
	}
}
