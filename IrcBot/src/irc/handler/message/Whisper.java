package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.settings.Config;

import org.pircbotx.hooks.events.MessageEvent;

public class Whisper extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String sub = event.getMessage().substring(event.getMessage().indexOf(" ") + 1);
		Startup.print("~INFO Response: whispered.");
		event.getBot().sendNotice(event.getMessage().split(" ")[1], sub.substring(sub.indexOf(" ") + 1));
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() > 10 && event.getMessage().substring(1, 9).equals("whisper ") && event.getMessage().split(" ").length > 3);
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("whisper")) {
			return chan.functions.get("whisper");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
