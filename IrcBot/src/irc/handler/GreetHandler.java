package irc.handler;

import irc.settings.Config;

import org.pircbotx.hooks.events.JoinEvent;

/**
 * TODO:
 * 
 * Custom channel greeting
 */
public class GreetHandler implements Runnable {

	private final JoinEvent<?> event;

	public GreetHandler(JoinEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		String greeting = Config.speech.get("GREET").replace("<name>", event.getUser().getNick());
		if (Config.channels.get(event.getChannel().getName()).defaults.get("greet_visible").equals("public")) {
			event.getBot().sendNotice(event.getChannel(), greeting);
		} else {
			event.getBot().sendNotice(event.getUser(), greeting);
		}
	}
}
