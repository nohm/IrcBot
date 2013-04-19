package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pircbotx.hooks.events.MessageEvent;

public class Anime extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String command = event.getMessage().split(" ")[1];
		String query;
		if (command.equals("get")) {
			query = event.getMessage().split("anime get ")[1];
		} else {
			query = "";
		}

		String response;
		try {
			String html = Utils.httpRequest("http://www.animetake.com/").toString();
			Document doc = Jsoup.parse(html);
			Elements list = doc.select("div[id=mainContent]").select("ul").select("li");
			ArrayList<Element> items = new ArrayList<Element>();
			for (Element e : list) {
				items.add(e.select("div.updateinfo").select("h4").select("a").get(0));
			}

			if (command.equals("list")) {
				response = "Latest releases: ";
				int index = (items.size() >= 5) ? 5 : items.size();
				if (index == 0) {
					response += "nothing found :(";
				} else {
					for (int i = 0; i < index; i++) {
						response += items.get(i).attr("title") + " | ";
					}
					response = response.substring(0, response.length() - 2);
				}
			} else {
				response = "Lastest releases for " + query + ": ";
				if (items.size() == 0) {
					response += "nothing found :(";
				} else {
					int index = 0;
					for (Element e : items) {
						if (index < 5 && (e.attr("title").toLowerCase().contains(query.toLowerCase()) || e.attr("title").toLowerCase().equals(query.toLowerCase()))) {
							response += e.attr("title") + " | ";
							index++;
						}
					}
					if (response.equals("Lastest releases for " + query + ": ")) {
						response += "nothing found :(";
					} else {
						response = response.substring(0, response.length() - 2);
					}
				}
			}
		} catch (Exception e) {
			response = "Error retrieving data :(";
		}
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() == 11 && event.getMessage().endsWith("anime list")) || (event.getMessage().length() >= 12 && event.getMessage().substring(1, 11).equals("anime get ")));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("anime")) {
			return chan.functions.get("anime");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

}
