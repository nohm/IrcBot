package org.snack.irc.main;

import org.pircbotx.PircBotX;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Configuration;

/**
 * Starts the bot up, gives it all the info it needs, adds an event handler and
 * makes it connect.
 * 
 * @author snack
 * 
 */
public class Startup {

	public static void main(String[] args) {
		Configuration.initialize();

		// Setup a new bot
		PircBotX bot = new PircBotX();

		// Fill in it's name, login, etc.
		bot.setName(Configuration.BOT_NAME);
		bot.setLogin(Configuration.BOT_LOGIN);
		bot.setVersion(Configuration.BOT_VERSION);

		// Toggle debugging
		bot.setVerbose(Configuration.DEBUG);

		// Give the bot a listener
		bot.getListenerManager().addListener(new SnackBot());

		// Connect to a server & channel
		try {
			bot.connect(Configuration.SERVER);
		} catch (Exception e) {
			try {
				Thread.sleep(5000);
				bot.setName(Configuration.BOT_ALT_NAME);
				bot.connect(Configuration.SERVER);
			} catch (Exception e1) {
				System.out.println("Couldn't connect, fix it or try later.");
				System.exit(-1);
			}
		}

		if (!Configuration.BOT_PASS.equals("") && Configuration.BOT_PASS != null) {
			bot.sendRawLine("NICKSERV IDENTIFY " + Configuration.BOT_PASS);
		}
		for (Chan channel : Configuration.CHANNELS.values()) {
			bot.sendRawLine("JOIN " + channel.getName());
		}
	}
}