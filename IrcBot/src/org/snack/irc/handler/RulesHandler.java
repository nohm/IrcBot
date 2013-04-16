package org.snack.irc.handler;

import org.pircbotx.hooks.events.JoinEvent;
import org.snack.irc.settings.Config;

/**
 * TODO:
 * 
 * Custom channel greeting
 */
public class RulesHandler implements Runnable {

	private final JoinEvent<?> event;

	public RulesHandler(JoinEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		String rules = Config.channels.get(event.getChannel().getName()).defaults.get("rules");
		for (String r : rules.split("~~!~~")) {
			event.getBot().sendNotice(event.getUser(), r);
		}
	}
}
