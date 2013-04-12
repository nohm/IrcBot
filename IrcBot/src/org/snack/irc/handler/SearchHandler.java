package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.model.FThread;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.FourChanAPI;

public class SearchHandler implements Runnable {

	private final MessageEvent<?> event;

	public SearchHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		search();
	}

	private void search() {
		String board;
		try {
			board = event.getMessage().split(" ")[0].split("-")[1];
		} catch (Exception e) {
			board = Config.channels.get(event.getChannel().getName()).search_default;
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
