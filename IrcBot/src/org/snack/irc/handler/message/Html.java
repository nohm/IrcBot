package org.snack.irc.handler.message;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.HtmlGetter;

public class Html extends TriggerHandler {

	private MessageEvent<?> event;

	public Html() {}

	public Html(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		getHTMLTitle();
	}

	/**
	 * Parses an event's text for http(s) links, cleans them and makes
	 * HtmlGetter return their titles.
	 */
	private void getHTMLTitle() {
		String[] split = event.getMessage().split(" ");
		ArrayList<String> links = new ArrayList<String>();
		for (String toLinks : split) {
			if (toLinks.startsWith("http://") || toLinks.startsWith("https://")) {
				links.add(toLinks);
			}
		}
		ArrayList<String> cleaned = new ArrayList<String>(links.size());
		for (String toClean : links) {
			if (toClean.endsWith(",") || toClean.endsWith(".") || toClean.endsWith(":") || toClean.endsWith(";")) {
				cleaned.add(toClean.substring(0, toClean.length() - 1));
			} else {
				cleaned.add(toClean);
			}
		}
		for (String toPrint : cleaned) {
			String response = HtmlGetter.getTitle(toPrint);
			Monitor.print("~RESPONSE " + response);
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("HT_TIT").replace("<title>", response));
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().contains("http://") || event.getMessage().contains("https://"));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("html")) {
			return chan.functions.get("html");
		} else {
			return true;
		}
	}
}
