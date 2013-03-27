package org.snack.irc.main;

import java.util.concurrent.Semaphore;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

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

	private final static boolean niceLookingMonitor = true;
	private static PircBotX bot;
	private static Semaphore restart;

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		if (niceLookingMonitor) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}

		try {
			// Read config
			Config.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		Monitor.getInstance();

		start();
	}

	public static void restart() {
		Monitor.print("Restarting");
		restart = new Semaphore(1);
		stop();
		restart.acquireUninterruptibly();
		start();
		Monitor.print("Restarted");
	}

	private static void stop() {
		Monitor.print("Stopping");
		bot.disconnect();
		bot = null;
		DatabaseManager.getInstance().closeConnection();
		restart.release();
		Monitor.print("Stopped");
	}

	private static void start() {
		Monitor.print("Starting");

		// Setup a new bot
		bot = new PircBotX();

		// Fill in it's name, login, etc.
		bot.setName(Config.sett_str.get("BOT_NAME"));
		bot.setLogin(Config.sett_str.get("BOT_LOGIN"));
		bot.setVersion(Config.sett_str.get("BOT_VERSION"));
		// Toggle debugging
		bot.setVerbose(Config.sett_bool.get("DEBUG"));
		// Give the bot a listener
		bot.getListenerManager().addListener(new BotListener(bot));
		Monitor.print("Initialized bot");

		// Connect to a server & channel
		try {
			bot.connect(Config.sett_str.get("SERVER"));
		} catch (Exception e) {
			try {
				Thread.sleep(5000);
				bot.setName(Config.sett_str.get("BOT_ALT_NAME"));
				bot.connect(Config.sett_str.get("SERVER"));
			} catch (Exception e1) {
				Monitor.print("Couldn't connect, fix it or try later.");
				System.exit(-1);
			}
		}
		Monitor.print("Joined server");

		// Authenticate
		if (!Config.sett_str.get("BOT_PASS").equals("") && Config.sett_str.get("BOT_PASS") != null) {
			bot.sendRawLine("NICKSERV IDENTIFY " + Config.sett_str.get("BOT_PASS"));
			Monitor.print("Authenticated");
		}
		// Join channels
		for (Chan channel : Config.channels.values()) {
			bot.sendRawLine("JOIN " + channel.name);
		}
		Monitor.print("Joined channels");

		// Start DB
		DatabaseManager.getInstance().initializeConnection();
		Monitor.print("Started");
		Monitor.print("--------------------------------------------------");
	}
}