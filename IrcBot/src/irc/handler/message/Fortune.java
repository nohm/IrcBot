package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.io.ByteArrayOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.pircbotx.hooks.events.MessageEvent;

public class Fortune extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			ByteArrayOutputStream output = Utils.httpRequest("http://www.fullerdata.com/FortuneCookie/showFortune.aspx");
			Document doc = Jsoup.parse(output.toString());
			Elements el = doc.select("td[valign=top]");
			response = el.text();
		} catch (Exception e) {
			response = "Error getting fortune :(";
		}
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() == 8 && event.getMessage().endsWith("fortune"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("fortune")) {
			return chan.functions.get("fortune");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

}
