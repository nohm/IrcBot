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
		Monitor.print("~INFO Restarting");
		restart = new Semaphore(1);
		stop();
		restart.acquireUninterruptibly();
		start();
		Monitor.print("~INFO Restarted");
	}

	private static void stop() {
		Monitor.print("~INFO Stopping");
		bot.disconnect();
		bot = null;
		DatabaseManager.getInstance().closeConnection();
		restart.release();
		Monitor.print("~INFO Stopped");
	}

	private static void start() {
		Monitor.print("~INFO Starting");

		// Setup a new bot
		bot = new PircBotX();

		// Fill in it's name, login, etc.
		bot.setName(Config.sett_str.get("BOT_NAME"));
		bot.setLogin(Config.sett_str.get("BOT_LOGIN"));
		bot.setVersion(Config.sett_str.get("BOT_VERSION"));
		// Toggle debugging
		bot.setVerbose(Config.sett_bool.get("DEBUG"));
		// Message delay
		bot.setMessageDelay(0);
		// Auto reconnect
		bot.setAutoReconnect(true);
		bot.setAutoReconnectChannels(true);
		// Give the bot a listener
		bot.getListenerManager().addListener(new BotListener(bot));
		Monitor.print("~INFO Initialized bot");

		// Connect to a server & channel
		try {
			bot.connect(Config.sett_str.get("SERVER"));
		} catch (Exception e) {
			try {
				Thread.sleep(5000);
				bot.setName(Config.sett_str.get("BOT_ALT_NAME"));
				bot.connect(Config.sett_str.get("SERVER"));
			} catch (Exception e1) {
				Monitor.print("~ERROR Couldn't connect, fix it or try later.");
				System.exit(-1);
			}
		}
		Monitor.print("~INFO Joined server");

		// Authenticate
		if (!Config.sett_str.get("BOT_PASS").equals("") && Config.sett_str.get("BOT_PASS") != null) {
			bot.sendRawLine("NICKSERV IDENTIFY " + Config.sett_str.get("BOT_PASS"));
			Monitor.print("~INFO Authenticated");
		}
		// Join channels
		for (Chan channel : Config.channels.values()) {
			if (channel.join) {
				bot.sendRawLine("JOIN " + channel.name);
			}
		}
		Monitor.print("~INFO Joined channels");

		// Start DB
		DatabaseManager.getInstance().initializeConnection();
		Monitor.print("~INFO Started");
	}
}