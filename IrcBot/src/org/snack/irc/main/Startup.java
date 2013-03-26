package org.snack.irc.main;

import java.util.concurrent.Semaphore;

import org.pircbotx.PircBotX;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

/**
 * Starts the bot up, gives it all the info it needs, adds an event handler and
 * makes it connect.
 * 
 * @author snack
 * 
 */
public class Startup {

	private static PircBotX bot;
	private static Semaphore restart;

	public static void main(String[] args) {
		start();
	}

	public static void restart() {
		restart = new Semaphore(1);
		stop();
		restart.acquireUninterruptibly();
		start();
	}

	private static void stop() {
		bot.disconnect();
		bot = null;
		DatabaseManager.getInstance().closeConnection();
		restart.release();
	}

	private static void start() {
		try {
			// Read config
			Config.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Setup a new bot
		bot = new PircBotX();

		// Fill in it's name, login, etc.
		bot.setName(Config.sett_str.get("BOT_NAME"));
		bot.setLogin(Config.sett_str.get("BOT_LOGIN"));
		bot.setVersion(Config.sett_str.get("BOT_VERSION"));
		// Toggle debugging
		bot.setVerbose(Config.sett_bool.get("DEBUG"));
		// Give the bot a listener
		bot.getListenerManager().addListener(new BotListener());

		// Connect to a server & channel
		try {
			bot.connect(Config.sett_str.get("SERVER"));
		} catch (Exception e) {
			try {
				Thread.sleep(5000);
				bot.setName(Config.sett_str.get("BOT_ALT_NAME"));
				bot.connect(Config.sett_str.get("SERVER"));
			} catch (Exception e1) {
				System.out.println("Couldn't connect, fix it or try later.");
				System.exit(-1);
			}
		}

		// Authenticate
		if (!Config.sett_str.get("BOT_PASS").equals("") && Config.sett_str.get("BOT_PASS") != null) {
			bot.sendRawLine("NICKSERV IDENTIFY " + Config.sett_str.get("BOT_PASS"));
		}
		// Join channels
		for (Chan channel : Config.channels.values()) {
			bot.sendRawLine("JOIN " + channel.getName());
		}

		// Start DB
		DatabaseManager.getInstance().initializeConnection();
		System.out.println("Booted succesfully.");
	}
}