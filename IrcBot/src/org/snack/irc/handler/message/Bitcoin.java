package org.snack.irc.handler.message;

import java.io.ByteArrayOutputStream;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Startup;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.main.Utils;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class Bitcoin extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			ByteArrayOutputStream output = Utils.httpRequest("http://data.mtgox.com/api/2/BTCUSD/money/ticker");
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONObject data = jo.getJSONObject("data");

			String buy = data.getJSONObject("buy").getString("display_short");
			String high = data.getJSONObject("high").getString("display_short");
			String low = data.getJSONObject("low").getString("display_short");
			String vol = data.getJSONObject("vol").getString("display_short");

			output = Utils.httpRequest("http://data.mtgox.com/api/1/generic/order/lag");
			jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			data = jo.getJSONObject("return");

			String lag = data.getString("lag_text");

			response = "Current: " + buy+ " - High: " + high + " - Low: " + low + " - Volume: " + vol + " - Lag: " + lag;
		} catch (Exception e) {
			response = "Error getting data :(";
		}
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() == 8 && event.getMessage().endsWith("bitcoin")) || (event.getMessage().length() == 9 && event.getMessage().endsWith("buttcoin")));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("bitcoin")) {
			return chan.functions.get("bitcoin");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

}
