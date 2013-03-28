package org.snack.irc.handler;

import org.pircbotx.hooks.events.JoinEvent;
import org.snack.irc.settings.Config;

public class GreetHandler implements Runnable {

	private final JoinEvent<?> event;

	public GreetHandler(JoinEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		greet();
	}

	/**
	 * Returns a greeting
	 */
	private void greet() {
		String greeting = Config.speech.get("GREET").replace("<name>", event.getUser().getNick());
		if (Config.channels.get(event.getChannel().getName()).greet_visible) {
			event.getBot().sendNotice(event.getChannel(), greeting);
		} else {
			event.getBot().sendNotice(event.getUser(), greeting);
		}
	}
}
