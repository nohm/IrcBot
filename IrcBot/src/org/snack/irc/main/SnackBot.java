package org.snack.irc.main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.pircbotx.Channel;
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
import org.snack.irc.settings.Config;
import org.snack.irc.settings.SettingParser;
import org.snack.irc.settings.SettingStorer;

/**
 * The handler for text events recieved by the bot.
 * 
 * @author snack
 * 
 */
@SuppressWarnings("rawtypes")
public class SnackBot extends ListenerAdapter implements Listener {

	/**
	 * Called on every message, determines what to do with it.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		Chan chan = Config.channels.get(event.getChannel().getName());
		// Call for weather
		if (event.getMessage().startsWith(",we ") || event.getMessage().equals(",we") || event.getMessage().startsWith(".we ") || event.getMessage().equals(".we")
				|| event.getMessage().startsWith("!we ") || event.getMessage().equals("!we")) {
			if (chan.getWeather() && !chan.getMute()) {
				WeatherHandler.getWeather(event);
			}

			// Call for now playing
		} else if (event.getMessage().startsWith(",np ") || event.getMessage().equals(",np") || event.getMessage().startsWith(".np ") || event.getMessage().equals(".np")
				|| event.getMessage().startsWith("!np ") || event.getMessage().equals("!np")) {
			if (chan.getLastfm() && !chan.getMute()) {
				LastfmHandler.getLastfm(event);
			}

			// Call for quotes
		} else if (event.getMessage().startsWith(",quote ") || event.getMessage().equals(",quote") || event.getMessage().startsWith(".quote ")
				|| event.getMessage().equals(".quote") || event.getMessage().startsWith("!quote ") || event.getMessage().equals("!quote")) {
			if (chan.getQuote() && !chan.getMute()) {
				QuoteHandler.getQuote(event);
			}

			// Call for HTML Title
		} else if (event.getMessage().contains("http://") || event.getMessage().contains("https://")) {
			if (chan.getHtml() && !chan.getMute()) {
				HtmlHandler.getHTMLTitle(event);
			}

			// Call for tell
		} else if (event.getMessage().startsWith(",tell ") || event.getMessage().startsWith(".tell ") || event.getMessage().startsWith("!tell ")) {
			if (chan.getTell() && !chan.getMute()) {
				TellHandler.add(event);
			}

			// Call for translate
		} else if (event.getMessage().startsWith(",translate ") || event.getMessage().startsWith(".translate ") || event.getMessage().startsWith("!translate ")) {
			if (chan.getTranslate() && !chan.getMute()) {
				TranslateHandler.translate(event);
			}

			// Call for romaji
		} else if (event.getMessage().startsWith(",romaji ") || event.getMessage().startsWith(".romaji ") || event.getMessage().startsWith("!romaji ")) {
			if (chan.getRomaji() && !chan.getMute()) {
				RomajiHandler.romaji(event);
			}

			// Call for katakana
		} else if (event.getMessage().startsWith(",katakana ") || event.getMessage().startsWith(".katakana ") || event.getMessage().startsWith("!katakana ")) {
			if (chan.getRomaji() && !chan.getMute()) {
				RomajiHandler.katakana(event);
			}

			// Call for help
		} else if (event.getMessage().startsWith(",help ") || event.getMessage().startsWith(".help ") || event.getMessage().startsWith("!help ")) {
			if (!chan.getMute()) {
				HelpHandler.sendHelp(event);
			}

			// Admin commands
		} else if (event.getMessage().startsWith(event.getBot().getNick() + ":")) {
			String nick = event.getBot().getNick();
			if (event.getUser().getNick().equals(Config.sett_str.get("ADMIN"))) {
				if (event.getMessage().equals(nick + ":mute") || event.getMessage().equals(nick + ":unmute")) {
					chan.setMute((event.getMessage().equals(nick + ":mute")) ? true : false);
					String response = chan.getMute() ? Config.speech.get("MUTE") : Config.speech.get("UNMUTE");
					event.getBot().sendMessage(event.getChannel(), response);
				} else if (event.getMessage().equals(nick + ":restart")) {
					Startup.restart();
				}
			}
			if (event.getMessage().equals(nick + ":we")) {
				WeatherHandler.getWeather(new MessageEvent(event.getBot(), event.getChannel(), event.getUser(), event.getMessage().replace(nick + ":", ".")));
			} else if (event.getMessage().equals(nick + ":np")) {
				LastfmHandler.getLastfm(new MessageEvent(event.getBot(), event.getChannel(), event.getUser(), event.getMessage().replace(nick + ":", ".")));
			}

			// Add random quotes
		} else {
			boolean add = false;
			if (new Random().nextInt(100) > 95) {
				add = true;
			}
			if (add) {
				try {
					ArrayList<String> storage = SettingParser.parseQuotes();
					storage.add(Config.speech.get("QU_SUC").replace("<name>", "<" + event.getUser().getNick() + ">").replace("<quote>", event.getMessage()));
					SettingStorer.storeQuotes(storage);
				} catch (Exception e) {
					e.printStackTrace();
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
	 *            The join orpart variable
	 */
	private void prepareTest(Event event, int ev) {
		for (Channel chan : event.getBot().getChannels()) {
			Set<User> users = event.getBot().getUsers(chan);
			Chan ch = Config.channels.get(chan.getName());
			for (User user : users) {
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
		for (Bot bot : chan.getBots()) {
			if (bot.getName().equals(nick)) {
				if (bot.getHtml() && chan.getFunc_html()) {
					chan.setHtml((event == 0) ? false : true);
					System.out.println(chan.getName() + " HTML: " + chan.getHtml());
				}
				if (bot.getLastfm() && chan.getFunc_lastfm()) {
					chan.setLastfm((event == 0) ? false : true);
					System.out.println(chan.getName() + " LASTFM: " + chan.getLastfm());
				}
				if (bot.getWeather() && chan.getFunc_weather()) {
					chan.setWeather((event == 0) ? false : true);
					System.out.println(chan.getName() + " WEATHER: " + chan.getWeather());
				}
				if (bot.getQuote() && chan.getFunc_quote()) {
					chan.setQuote((event == 0) ? false : true);
					System.out.println(chan.getName() + " QUOTE: " + chan.getQuote());
				}
				if (bot.getTell() && chan.getFunc_tell()) {
					chan.setTell((event == 0) ? false : true);
					System.out.println(chan.getName() + " TELL: " + chan.getTell());
				}
				if (bot.getTranslate() && chan.getFunc_translate()) {
					chan.setTranslate((event == 0) ? false : true);
					System.out.println(chan.getName() + " TRANSLATE: " + chan.getTranslate());
				}
				if (bot.getRomaji() && chan.getFunc_romaji()) {
					chan.setRomaji((event == 0) ? false : true);
					System.out.println(chan.getName() + " ROMAJI: " + chan.getRomaji());
				}
			}
		}
	}
}