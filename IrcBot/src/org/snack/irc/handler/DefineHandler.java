package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.worker.DictionaryAPI;

public class DefineHandler implements Runnable {

	private final MessageEvent<?> event;

	public DefineHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		define();
	}

	private void define() {
		String response = DictionaryAPI.search(event.getMessage().substring(event.getMessage().indexOf(" ") + 1));
		Monitor.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}
}
