package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.worker.DictionaryAPI;

public class Define extends TriggerHandler {

	private MessageEvent<?> event;

	public Define() {}

	public Define(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		define();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 7 && event.getMessage().substring(1, 7).equals("define"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("define")) {
			return chan.functions.get("define");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	private void define() {
		String response = DictionaryAPI.search(event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
		Monitor.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}
}
