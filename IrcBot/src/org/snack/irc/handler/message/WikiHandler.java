package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.worker.WikiAPI;

public class WikiHandler extends TriggerHandler {

	private MessageEvent<?> event;

	public WikiHandler() {}

	public WikiHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		searchWiki();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 5 && event.getMessage().substring(1, 5).equals("wiki"));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	private void searchWiki() {
		String language;
		try {
			language = event.getMessage().split(" ")[0].split("-")[1];
		} catch (Exception e) {
			language = "en";
		}

		String response = WikiAPI.search(language, event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
		Monitor.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}
}
