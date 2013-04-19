package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.model.FThread;
import irc.settings.Config;
import irc.worker.FourChanAPI;

import org.pircbotx.hooks.events.MessageEvent;

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
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() > 8 && event.getMessage().substring(1, 8).equals("search ")) || (event.getMessage().length() >= 11 && event.getMessage().split(" ").length >= 2 && event.getMessage().substring(1, 8).equals("search-")));
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
