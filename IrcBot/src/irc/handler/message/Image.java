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

public class Image extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		try {
			String query;
			if (event.getMessage().startsWith(">")) {
				query = event.getMessage().substring(1);
			} else {
				query = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
			}
			String uriWord = Utils.encodeQuery(query, true);

			ByteArrayOutputStream output  = Utils.httpRequest("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&safe=moderate&q=" + uriWord);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONObject response = jo.getJSONObject("responseData");
			JSONArray results = response.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			String imgUrl = result.getString("unescapedUrl");

			Startup.print("~INFO Response: " + query + ": " + imgUrl);
			event.getBot().sendMessage(event.getChannel(), query + ": " + imgUrl);
		} catch (Exception e) {
			if (!event.getMessage().startsWith(">")) {
				Startup.print("~INFO Response: Error getting image :(");
				event.getBot().sendMessage(event.getChannel(), "Error getting image :(");
			}
			// Return nothing
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return ((Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ").length >= 2 && event.getMessage().length() > 7 && event.getMessage().substring(1, 7).equals("image "))
				|| (event.getMessage().startsWith(">") && ((event.getMessage().endsWith(".tiff") ||	event.getMessage().endsWith(".TIFF") ||	event.getMessage().endsWith(".png") ||	event.getMessage().endsWith(".PNG") ||	event.getMessage().endsWith(".jpg") ||	event.getMessage().endsWith(".JPG") ||	event.getMessage().endsWith(".jpeg") ||	event.getMessage().endsWith(".JPEG") || event.getMessage().endsWith(".gif") ||	event.getMessage().endsWith(".GIF")))));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("image")) {
			return chan.functions.get("image");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}