package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Startup;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.model.FThread;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.FourChanAPI;

public class Search extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
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
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 7 && event.getMessage().substring(1, 7).equals("search"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("search")) {
			return chan.functions.get("search");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
