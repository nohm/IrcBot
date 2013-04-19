package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.settings.Config;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.pircbotx.hooks.events.MessageEvent;

public class Translater extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		HashMap<String, String> languages = getHashMap();
		String message = event.getMessage();
		String text = message.substring(11); // Cut off the command

		String from = "auto";
		String to = "auto";

		if (message.split(" ").length >= 4 && message.split(" ")[1].equals("from") && languages.containsKey(message.split(" ")[2])) {
			from = languages.get(message.split(" ")[2]);
			text = message.substring(message.indexOf(message.split(" ")[2]) + message.split(" ")[4].length() + 1);
		}
		if (message.split(" ").length >= 4 && message.split(" ")[1].equals("to") && languages.containsKey(message.split(" ")[2])) {
			to = languages.get(message.split(" ")[2]);
			text = message.substring(message.indexOf(message.split(" ")[2]) + message.split(" ")[2].length() + 1);
		} else if (message.split(" ").length >= 6 && !from.equals("") && message.split(" ")[3].equals("to") && languages.containsKey(message.split(" ")[4])) {
			to = languages.get(message.split(" ")[4]);
			text = message.substring(message.indexOf(message.split(" ")[4]) + message.split(" ")[4].length() + 1);
		}

		String response;

		try {
			text = URLEncoder.encode(text, "utf-8");

			URL url = new URL("http://translate.google.com/m?hl=" + to + "&sl=" + from + "&q=" + text + "&ie=UTF-8&oe=UTF-8");
			URLConnection urlConn = url.openConnection();
			urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(urlConn.getInputStream(), output);

			String html = output.toString();
			Document doc = Jsoup.parse(html, "UTF-8");
			Elements element = doc.select("div[dir=ltr]");
			String translated = element.text();

			response = Config.speech.get("TR_SUC").replace("<from>", from).replace("<to>", to).replace("<response>", translated);
		} catch (Exception e) {
			response = Config.speech.get("TR_ERR");
		}

		Startup.print("~RESPONSE  Translate: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 11 && event.getMessage().substring(1, 11).equals("translate "));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("translate")) {
			return chan.functions.get("translate");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	private static HashMap<String, String> languages;

	private static HashMap<String, String> getHashMap() {
		if (languages == null)  {
			languages = new HashMap<String, String>();

			languages.put("italian", "it");
			languages.put("japanese", "ja");
			languages.put("arabic", "ar");
			languages.put("korean", "ko");
			languages.put("bulgarian", "bg");
			languages.put("latvian", "lv");
			languages.put("catalan", "ca");
			languages.put("lithuanian", "lt");
			languages.put("chinese-simplified", "zh-CHS");
			languages.put("chinese-traditional", "zh-CHT");
			languages.put("malay", "ma");
			languages.put("norwegian", "no");
			languages.put("czech", "cs");
			languages.put("persian", "fa");
			languages.put("danish", "da");
			languages.put("polish", "pl");
			languages.put("dutch", "nl");
			languages.put("portuguese", "pt");
			languages.put("english", "en");
			languages.put("romanian", "ro");
			languages.put("estonian", "et");
			languages.put("russian", "ru");
			languages.put("finnish", "fi");
			languages.put("slovak", "sk");
			languages.put("french", "fr");
			languages.put("slovanian", "sl");
			languages.put("german", "de");
			languages.put("spanish", "es");
			languages.put("greek", "el");
			languages.put("swedish", "sv");
			languages.put("haitian-creole", "ht");
			languages.put("thai", "th");
			languages.put("hebrew", "he");
			languages.put("turkish", "tr");
			languages.put("hindi", "hi");
			languages.put("ukrainian", "uk");
			languages.put("hmong-daw", "mww");
			languages.put("urdu", "ur");
			languages.put("hungarian", "hu");
			languages.put("vietnamese", "vi");
			languages.put("indonesian", "id");
		}
		return languages;
	}
}