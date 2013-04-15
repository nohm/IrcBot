package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.FThread;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.FourChanAPI;

public class SearchHandler extends TriggerHandler {

	private MessageEvent<?> event;

	public SearchHandler() {}

	public SearchHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		search();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 7 && event.getMessage().substring(1, 7).equals("search"));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	private void search() {
		String board;
		try {
			board = event.getMessage().split(" ")[0].split("-")[1];
		} catch (Exception e) {
			board = Config.channels.get(event.getChannel().getName()).defaults.get("search_default");
		}

		String response;
		try {
			FThread[] threads = FourChanAPI.search(board, event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
			if (threads.length <= 0) {
				throw new Exception();
			}
			// Return all?
			response = Config.speech.get("SE_SUC").replace("<board>", board).replace("<subject>", threads[0].subject).replace("<url>", threads[0].url)
					.replace("<replies>", threads[0].replies).replace("<comment>", threads[0].comment);
		} catch (Exception e) {
			response = Config.speech.get("SE_ERR");
		}
		Monitor.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}
}
