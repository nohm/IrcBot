package org.snack.irc.main;

import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.handler.HelpHandler;
import org.snack.irc.handler.HtmlHandler;
import org.snack.irc.handler.LastfmHandler;
import org.snack.irc.handler.QuoteHandler;
import org.snack.irc.handler.RomajiHandler;
import org.snack.irc.handler.TellHandler;
import org.snack.irc.handler.TranslateHandler;
import org.snack.irc.handler.WeatherHandler;
import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.model.Quote;
import org.snack.irc.settings.Config;

/**
 * The handler for text events recieved by the bot.
 * 
 * @author snack
 * 
 */
@SuppressWarnings("rawtypes")
public class BotListener extends ListenerAdapter implements Listener {

	/**
	 * Called on every message, determines what to do with it.
	 */
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		PircBotX bot = event.getBot();
		Channel channel = event.getChannel();
		Chan chan = Config.channels.get(channel.getName());
		String message = event.getMessage();
		User user = event.getUser();
		String nick = user.getNick();

		// Command handler
		if (Config.sett_str.get("IDENTIFIERS").contains(message.substring(0, 1))) {

			// Call for weather
			if (message.substring(1, 3).equals("we")) {
				if (chan.getWeather() && !chan.getMute()) {
					new WeatherHandler(event).run();
					System.out.println("Weather: " + nick);
				}

				// Call for now playing
			} else if (message.substring(1, 3).equals("np")) {
				if (chan.getLastfm() && !chan.getMute()) {
					new LastfmHandler(event).run();
					System.out.println("Lastfm: " + nick);
				}

				// Cal for help
			} else if (message.substring(1, 5).equals("help")) {
				if (!chan.getMute()) {
					new HelpHandler(event).run();
					System.out.println("Helped: " + nick);
				}

				// Call for quotes
			} else if (message.substring(1, 6).equals("quote")) {
				if (chan.getQuote() && !chan.getMute()) {
					new QuoteHandler(event).run();
					System.out.println("Quote: " + nick);
				}

				// Call for tell
			} else if (message.substring(1, 6).equals("tell ")) {
				if (chan.getTell() && !chan.getMute()) {
					new TellHandler(event, null, true).run();
					System.out.println("Tell: " + nick);
				}

				// Call for romaji
			} else if (message.substring(1, 8).equals("romaji ")) {
				if (chan.getRomaji() && !chan.getMute()) {
					new RomajiHandler(event, true).run();
					System.out.println("Romaji: " + nick);
				}

				// Call for katakana
			} else if (message.substring(1, 10).equals("katakana ")) {
				if (chan.getRomaji() && !chan.getMute()) {
					new RomajiHandler(event, false).run();
					System.out.println("Katakana: " + nick);
				}

				// Call for translate
			} else if (message.substring(1, 11).equals("translate ")) {
				if (chan.getTranslate() && !chan.getMute()) {
					new TranslateHandler(event).run();
					System.out.println("Translate: " + nick);
				}
			}

			// Call for HTML Title
		} else if (message.contains("http://") || message.contains("https://")) {
			if (chan.getHtml() && !chan.getMute()) {
				new HtmlHandler(event).run();
				System.out.println("Html: " + nick);
			}

			// Admin commands
		} else if (message.startsWith(bot.getNick() + ":")) {
			if (user.getNick().equals(Config.sett_str.get("ADMIN"))) {
				if (message.equals(nick + ":mute") || message.equals(nick + ":unmute")) {
					chan.setMute((message.equals(nick + ":mute")) ? true : false);
					bot.sendMessage(channel, chan.getMute() ? Config.speech.get("MUTE") : Config.speech.get("UNMUTE"));
				} else if (message.equals(nick + ":restart")) {
					Startup.restart();
				}
			}

		} else {
			boolean add = true;
			for (Bot b : chan.getBots()) {
				if (b.getName().equalsIgnoreCase(nick)) {
					add = false;
				}
			}
			if (Config.sett_str.get("BOT_NAME").equalsIgnoreCase(nick)) {
				add = false;
			}
			// Add random quotes
			if (new Random().nextInt(100) > 95 && add && message.length() <= Config.sett_int.get("QUOTE_MAX") && message.length() >= Config.sett_int.get("QUOTE_MIN")) {
				try {
					System.out.println("Added quote: " + chan.getName() + " <" + user.getNick() + "> " + message);
					DatabaseManager.getInstance().putQuote(new Quote(chan.getName(), user.getNick(), message));
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Auto-rejoin on kick.
	 */
	@Override
	public void onKick(KickEvent event) {
		event.getBot().sendRawLine("JOIN " + event.getChannel().getName());
	}

	/**
	 * Called whenever someone (or the bot) joins, updates the functions.
	 */
	@Override
	public void onJoin(JoinEvent event) throws Exception {
		new FunctionTester(event, 0).run();
		new TellHandler(null, event, false).run();
		System.out.println("Cleaned tells: " + event.getChannel().getName());
	}

	/**
	 * Called whenever someone (or the bot) parts/leaves, updates the functions.
	 */
	@Override
	public void onPart(PartEvent event) {
		new FunctionTester(event, 1).run();
	}

	/**
	 * Called when the bot has fully started, updates the functions.
	 */
	@Override
	public void onUserList(UserListEvent event) {
		new FunctionTester(event, 0).run();
	}

	/**
	 * Called when the connection gets lost, will retry to join every 5000ms
	 */
	@Override
	public void onDisconnect(DisconnectEvent event) throws Exception {
		try {
			event.getBot().connect(Config.sett_str.get("SERVER"));
			if (!Config.sett_str.get("BOT_PASS").equals("") && Config.sett_str.get("BOT_PASS") != null) {
				event.getBot().sendRawLine("NICKSERV IDENTIFY " + Config.sett_str.get("BOT_PASS"));
			}
			for (Chan channel : Config.channels.values()) {
				event.getBot().sendRawLine("JOIN " + channel.getName());
			}
		} catch (Exception e) {
			Thread.sleep(5000);
			onDisconnect(event);
		}
	}
}