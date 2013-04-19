package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;

import org.pircbotx.hooks.events.MessageEvent;

public class Tetete extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response = "tetete " + event.getUser().getNick() + event.getUser().getNick() + event.getUser().getNick();
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().equalsIgnoreCase("tetete"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("tetete")) {
			return chan.functions.get("tetete");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
