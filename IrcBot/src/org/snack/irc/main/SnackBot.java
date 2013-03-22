package org.snack.irc.main;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.pircbotx.Channel;
import org.pircbotx.User;
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
import org.snack.irc.handler.TellHandler;
import org.snack.irc.handler.WeatherHandler;
import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Configuration;
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
		Chan chan = Configuration.CHANNELS.get(event.getChannel().getName());
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

			// Call for help
		} else if (event.getMessage().startsWith(",help ") || event.getMessage().startsWith(".help ") || event.getMessage().startsWith("!help ")) {
			if (!chan.getMute()) {
				HelpHandler.sendHelp(event);
			}

			// Admin commands
		} else if (event.getMessage().startsWith("snackbot:")) {
			if (event.getUser().getNick().equals(Configuration.ADMIN)) {
				if (event.getMessage().equals("snackbot:mute") || event.getMessage().equals("snackbot:unmute")) {
					chan.setMute((event.getMessage().equals("snackbot:mute")) ? true : false);
					String response = chan.getMute() ? "I'll be silent." : "Yay! I can speak again.";
					event.getBot().sendMessage(event.getChannel(), response);
				} else if (event.getMessage().equals("snackbot:we")) {
					WeatherHandler.getWeather(new MessageEvent(event.getBot(), event.getChannel(), event.getUser(), ".we"));
				} else if (event.getMessage().equals("snackbot:np")) {
					LastfmHandler.getLastfm(new MessageEvent(event.getBot(), event.getChannel(), event.getUser(), ".np"));
				} else if (event.getMessage().equals("snackbot:restart")) {
					Startup.restart();
				}
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
					storage.add("<" + event.getUser().getNick() + "> " + event.getMessage());
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
		testFunctions(Configuration.CHANNELS.get(event.getChannel().getName()), event.getUser().getNick(), 0);
		TellHandler.tell(event);
	}

	/**
	 * Called whenever someone (or the bot) parts/leaves, updates the functions.
	 */
	@Override
	public void onPart(PartEvent event) {
		testFunctions(Configuration.CHANNELS.get(event.getChannel().getName()), event.getUser().getNick(), 1);
	}

	/**
	 * Called when the bot has fully started, updates the functions.
	 */
	@Override
	public void onUserList(UserListEvent event) {
		for (Channel chan : event.getBot().getChannels()) {
			Set<User> users = event.getBot().getUsers(chan);
			for (User user : users) {
				for (Bot b : Configuration.BOTS) {
					if (b.getName().equalsIgnoreCase(user.getNick())) {
						testFunctions(Configuration.CHANNELS.get(chan.getName()), b.getName(), 0);
					}
				}
			}
		}
	}

	/**
	 * Called when the connection gets lost, will retry to join every 5000ms
	 */
	@Override
	public void onDisconnect(DisconnectEvent event) throws Exception {
		try {
			event.getBot().connect(Configuration.SERVER);
			if (!Configuration.BOT_PASS.equals("") && Configuration.BOT_PASS != null) {
				event.getBot().sendRawLine("NICKSERV IDENTIFY " + Configuration.BOT_PASS);
			}
			for (Chan channel : Configuration.CHANNELS.values()) {
				event.getBot().sendRawLine("JOIN " + channel.getName());
			}
		} catch (Exception e) {
			Thread.sleep(5000);
			onDisconnect(event);
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
		for (Bot bot : Configuration.BOTS) {
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
			}
		}
	}
}