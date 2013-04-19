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

public class Fml extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			ByteArrayOutputStream output = Utils.httpRequest("http://www.fmylife.com/random/");
			String html = output.toString();

			Document doc = Jsoup.parse(html, "UTF-8");
			Elements element = doc.select("div.post.article");
			String id = element.attr("id");
			String text = element.select("p").get(0).text();

			response =  "(#" + id + ") " + text;
		} catch (Exception e) {
			response = "Error getting fml :(";
		}
		Startup.print("~INFO Response:" + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() == 4 && event.getMessage().endsWith("fml"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("fml")) {
			return chan.functions.get("fml");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

}
