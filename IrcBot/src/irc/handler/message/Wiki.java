package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.io.ByteArrayOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import org.pircbotx.hooks.events.MessageEvent;

public class Wiki extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String language;
		try {
			language = event.getMessage().split(" ")[0].split("-")[1];
		} catch (Exception e) {
			language = "en";
		}

		String response;
		try {
			String lang = language.toLowerCase();
			String searchWord = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
			String uriWord = Utils.encodeQuery(searchWord, true);

			ByteArrayOutputStream output = Utils.httpRequest("https://" + lang + ".wikipedia.org/w/api.php?action=opensearch&limit=1&format=json&search=" + uriWord);
			JSONArray jo = (JSONArray) JSONSerializer.toJSON(output.toString());

			JSONArray pages = jo.getJSONArray(1);
			if (pages.size() == 0) {
				response = Config.speech.get("WI_NO");
			} else {
				String link = "https://" + lang + ".wikipedia.org/wiki/" + pages.getString(0).replace(" ", "%20");
				response = Config.speech.get("WI_SUC").replace("<url>", link);
			}
		} catch (Exception e) {
			response = Config.speech.get("WI_ERR");
		}
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 5 && event.getMessage().substring(1, 5).equals("wiki"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("wiki")) {
			return chan.functions.get("wiki");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
