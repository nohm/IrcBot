package org.snack.irc.main;

import java.util.Random;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
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

			// Cal for help
			if (message.substring(1, 5).equals("help")) {
				if (!chan.getMute()) {
					HelpHandler.sendHelp(event);
					System.out.println("Helped: " + nick);
				}

				// Call for weather
			} else if (message.substring(1, 3).equals("we")) {
				if (chan.getWeather() && !chan.getMute()) {
					WeatherHandler.getWeather(event);
					System.out.println("Weather: " + nick);
				}

				// Call for now playing
			} else if (message.substring(1, 3).equals("np")) {
				if (chan.getLastfm() && !chan.getMute()) {
					LastfmHandler.getLastfm(event);
					System.out.println("Lastfm: " + nick);
				}

				// Call for quotes
			} else if (message.substring(1, 6).equals("quote")) {
				if (chan.getQuote() && !chan.getMute()) {
					QuoteHandler.getQuote(event);
					System.out.println("Quote: " + nick);
				}

				// Call for tell
			} else if (message.substring(1, 6).equals("tell ")) {
				if (chan.getTell() && !chan.getMute()) {
					TellHandler.add(event);
					System.out.println("Tell: " + nick);
				}

				// Call for translate
			} else if (message.substring(1, 11).equals("translate ")) {
				if (chan.getTranslate() && !chan.getMute()) {
					TranslateHandler.translate(event);
					System.out.println("Translate: " + nick);
				}

				// Call for romaji
			} else if (message.substring(1, 8).equals("romaji ")) {
				if (chan.getRomaji() && !chan.getMute()) {
					RomajiHandler.romaji(event);
					System.out.println("Romaji: " + nick);
				}

				// Call for katakana
			} else if (message.substring(1, 10).equals("katakana ")) {
				if (chan.getRomaji() && !chan.getMute()) {
					RomajiHandler.katakana(event);
					System.out.println("Katakana: " + nick);
				}
			}

			// Call for HTML Title
		} else if (message.contains("http://") || message.contains("https://")) {
			if (chan.getHtml() && !chan.getMute()) {
				HtmlHandler.getHTMLTitle(event);
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
		prepareTest(event, 0);
		TellHandler.tell(event);
		System.out.println("Cleaned tells: " + event.getChannel().getName());
	}

	/**
	 * Called whenever someone (or the bot) parts/leaves, updates the functions.
	 */
	@Override
	public void onPart(PartEvent event) {
		prepareTest(event, 1);
	}

	/**
	 * Called when the bot has fully started, updates the functions.
	 */
	@Override
	public void onUserList(UserListEvent event) {
		prepareTest(event, 0);
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

	/**
	 * Prepares testing all users on all channels
	 * 
	 * @param event
	 *            The event to get the user/bot from
	 * @param ev
	 *            The join or part variable
	 */
	private void prepareTest(Event event, int ev) {
		for (Channel chan : event.getBot().getChannels()) {
			Chan ch = Config.channels.get(chan.getName());
			for (User user : event.getBot().getUsers(chan)) {
				for (Bot b : ch.getBots()) {
					if (b.getName().equalsIgnoreCase(user.getNick())) {
						testFunctions(Config.channels.get(chan.getName()), b.getName(), ev);
					}
				}
			}
		}
	}

	/**
	 * Tests whether each function should be on or off per channel
	 * 
	 * @param chan
	 *            The channel in question
	 * @param nick
	 *            The nick of the user that joined/parted/left
	 * @param event
	 *            Is it a join(0) or a part/leave(0)?
	 */
	private void testFunctions(Chan chan, String nick, int event) {
		System.out.println(((event == 0) ? "Join: " : "Part: ") + chan.getName() + " " + nick);
		for (Bot bot : chan.getBots()) {
			if (bot.getName().equals(nick)) {
				if (bot.getHtml() && chan.getFunc_html()) {
					chan.setHtml((event == 0) ? false : true);
				}
				if (bot.getLastfm() && chan.getFunc_lastfm()) {
					chan.setLastfm((event == 0) ? false : true);
				}
				if (bot.getWeather() && chan.getFunc_weather()) {
					chan.setWeather((event == 0) ? false : true);
				}
				if (bot.getQuote() && chan.getFunc_quote()) {
					chan.setQuote((event == 0) ? false : true);
				}
				if (bot.getTell() && chan.getFunc_tell()) {
					chan.setTell((event == 0) ? false : true);
				}
				if (bot.getTranslate() && chan.getFunc_translate()) {
					chan.setTranslate((event == 0) ? false : true);
				}
				if (bot.getRomaji() && chan.getFunc_romaji()) {
					chan.setRomaji((event == 0) ? false : true);
				}
				System.out.println("Functions: html:" + chan.getHtml() + " lastfm:" + chan.getLastfm() + " weather:" + chan.getWeather() + " quote:" + chan.getQuote() + " tell:"
						+ chan.getTell() + " translate:" + chan.getTranslate() + " romaji:" + chan.getRomaji());
			}
		}
	}
}