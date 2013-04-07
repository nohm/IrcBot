package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.worker.WikiAPI;

public class WikiHandler implements Runnable {

	private final MessageEvent<?> event;

	public WikiHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		searchWiki();
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
