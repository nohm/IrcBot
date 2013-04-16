package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Startup;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;

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
