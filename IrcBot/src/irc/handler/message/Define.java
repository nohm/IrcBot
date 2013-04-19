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

public class Define extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			String searchWord = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
			String uriWord = Utils.encodeQuery(searchWord, true);

			ByteArrayOutputStream output  = Utils.httpRequest("http://api.pearson.com/v2/dictionaries/entries?headword=" + uriWord + "&apikey=f1f4514986ce72d85fb82db55fa4f3c7");
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONArray results = jo.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			String type = result.getString("part_of_speech");
			JSONArray senses = result.getJSONArray("senses");
			JSONObject sense = senses.getJSONObject(0);
			String definition = sense.getString("definition");

			response = Config.speech.get("DE_SUC").replace("<query>", uriWord).replace("<type>", type).replace("<definition>", definition);
		} catch (Exception e) {
			response =  Config.speech.get("DE_ERR");
		}
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 7 && event.getMessage().substring(1, 7).equals("define"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("define")) {
			return chan.functions.get("define");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
