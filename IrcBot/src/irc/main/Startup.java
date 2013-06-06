package irc.main;

import irc.database.DatabaseManager;
import irc.model.Chan;
import irc.settings.Config;

import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.pircbotx.PircBotX;
import org.pircbotx.UtilSSLSocketFactory;

/**
 * Starts the bot up, gives it all the info it needs, adds an event handler and
 * makes it connect.
 * 
 * @author snack
 * 
 */
public class Startup {

	private final static boolean niceLookingStartup = true;
	private static PircBotX bot;
	private static Semaphore restart;
	private static Logger logger;

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		if (niceLookingStartup) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}

		try {
			// Read config
			Config.initialize();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
			logger.setLevel(Level.INFO);
			FileHandler handler = new FileHandler(Config.sett_str.get("LOG_LOC"));
			handler.setFormatter(new SimpleFormatter());
			logger.addHandler(handler);
		} catch (Exception e) {
			Startup.print("~ERROR Couldn't create log file");
		}

		start();
	}

	public static void print(String message) {
		Startup.logger.info(message);
	}

	public static void restart() {
		Startup.print("~INFO Restarting");
		restart = new Semaphore(1);
		stop();
		restart.acquireUninterruptibly();
		start();
		Startup.print("~INFO Restarted");
	}

	private static void stop() {
		Startup.print("~INFO Stopping");
		bot.disconnect();
		bot = null;
		DatabaseManager.getInstance().closeConnection();
		restart.release();
		Startup.print("~INFO Stopped");
	}

	private static void start() {
		Startup.print("~INFO Starting");

		// Setup a new bot
		bot = new PircBotX();

		// Fill in it's name, login, etc.
		bot.setName(Config.sett_str.get("BOT_NAME"));
		bot.setLogin(Config.sett_str.get("BOT_LOGIN"));
		bot.setVersion(Config.sett_str.get("BOT_VERSION"));
		// Toggle debugging
		bot.setVerbose(Config.sett_bool.get("DEBUG"));
		// Message delay
		bot.setMessageDelay(100);
		// Auto reconnect
		bot.setAutoReconnect(true);
		bot.setAutoReconnectChannels(true);
		// Give the bot a listener
		bot.getListenerManager().addListener(new BotListener(bot));
		Startup.print("~INFO Initialized bot");

		// Connect to a server & channel
		try {
			if (Config.sett_str.get("SSL").equals("true")) {
				bot.connect(Config.sett_str.get("SERVER"), Integer.valueOf(Config.sett_str.get("PORT")), new UtilSSLSocketFactory().trustAllCertificates());
			} else {
				bot.connect(Config.sett_str.get("SERVER"), Integer.valueOf(Config.sett_str.get("PORT")));
			}
		} catch (Exception e) {
			try {
				Thread.sleep(5000);
				bot.setName(Config.sett_str.get("BOT_ALT_NAME"));
				if (Config.sett_str.get("SSL").equals("true")) {
					bot.connect(Config.sett_str.get("SERVER"), Integer.valueOf(Config.sett_str.get("PORT")), new UtilSSLSocketFactory().trustAllCertificates());
				} else {
					bot.connect(Config.sett_str.get("SERVER"), Integer.valueOf(Config.sett_str.get("PORT")));
				}
			} catch (Exception e1) {
				Startup.print("~ERROR Couldn't connect, fix it or try later.");
				System.exit(-1);
			}
		}
		Startup.print("~INFO Joined server");

		// Authenticate
		if (!Config.sett_str.get("BOT_PASS").equals("") && Config.sett_str.get("BOT_PASS") != null) {
			bot.sendRawLine("NICKSERV IDENTIFY " + Config.sett_str.get("BOT_PASS"));
			Startup.print("~INFO Authenticated");
		}
		// Join channels
		for (Chan channel : Config.channels.values()) {
			if (channel.join) {
				bot.sendRawLine("JOIN " + channel.name);
			}
		}
		Startup.print("~INFO Joined channels");

		// Start DB
		DatabaseManager.getInstance().initializeConnection();
		Startup.print("~INFO Started");
	}
}