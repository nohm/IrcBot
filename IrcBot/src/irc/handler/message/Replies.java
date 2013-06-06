package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.settings.Config;
import irc.worker.FourChanAPI;

import org.pircbotx.hooks.events.MessageEvent;

public class Replies extends TriggerHandler {

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
			response = FourChanAPI.getMostReplies(board);
		} catch (Exception e) {
			response = "Unable to get data :(";
		}
		Startup.print("~RESPONSE " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() == 8 && event.getMessage().substring(1, 8).equals("replies")) || event.getMessage().length() > 9 && event.getMessage().substring(1, 9).equals("replies-"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("replies")) {
			return chan.functions.get("replies");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
