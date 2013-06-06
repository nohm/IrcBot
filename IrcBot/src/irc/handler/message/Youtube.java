package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.settings.Config;
import irc.worker.YoutubeAPI;

import org.pircbotx.hooks.events.MessageEvent;

public class Youtube extends TriggerHandler implements Runnable {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			response = YoutubeAPI.search(event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
		} catch (Exception e) {
			response = "Error getting result :(";
		}
		Startup.print("~INFO Response:" + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ").length > 1 && event.getMessage().substring(1, 8).equals("youtube"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("youtube")) {
			return chan.functions.get("youtube");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
