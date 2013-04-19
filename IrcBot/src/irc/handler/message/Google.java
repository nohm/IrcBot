package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.io.ByteArrayOutputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.pircbotx.hooks.events.MessageEvent;

public class Google extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			String query = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
			String uriWord = Utils.encodeQuery(query, true);

			ByteArrayOutputStream output = Utils.httpRequest("https://ajax.googleapis.com/ajax/services/search/web?v=1.0&safe=moderate&q=" + uriWord);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONObject responseData = jo.getJSONObject("responseData");
			JSONArray results = responseData.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			String imgUrl = result.getString("unescapedUrl");

			response = event.getMessage().substring(event.getMessage().indexOf(" ") + 1) + ": " + imgUrl;
		} catch (Exception e) {
			response = "Error getting result :(";
		}
		Startup.print("~INFO Response:" + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ").length >= 2 &&event.getMessage().length() > 8 && event.getMessage().substring(1, 8).equals("google "));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("google")) {
			return chan.functions.get("google");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}