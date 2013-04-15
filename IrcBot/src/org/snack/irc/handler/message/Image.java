package org.snack.irc.handler.message;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class Image extends TriggerHandler {

	private MessageEvent<?> event;

	public Image() {}

	public Image(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		getImage();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return ((Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ").length >= 2 && event.getMessage().length() > 7 && event.getMessage().substring(1, 7).equals("image "))
				|| (event.getMessage().startsWith(">") && ((event.getMessage().endsWith(".jpg") || event.getMessage().endsWith(".png") || event.getMessage().endsWith(".gif")))));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("image")) {
			return chan.functions.get("image");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	private void getImage() {
		try {
			String query;
			if (event.getMessage().startsWith(">")) {
				query = event.getMessage().substring(1);
			} else {
				query = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
			}
			String searchWord = query.replaceAll("[^\\p{L}]", "").toLowerCase();
			String uriWord = URLEncoder.encode(searchWord, "utf-8");

			URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?v=1.0&safe=moderate&q=" + uriWord);
			URLConnection urlConn = url.openConnection();
			urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(urlConn.getInputStream(), output);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONObject response = jo.getJSONObject("responseData");
			JSONArray results = response.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			String imgUrl = result.getString("unescapedUrl");

			Monitor.print("~INFO Response: " + query + ": " + imgUrl);
			event.getBot().sendMessage(event.getChannel(), query + ": " + imgUrl);
		} catch (Exception e) {
			e.printStackTrace();
			// Return nothing
		}
	}
}