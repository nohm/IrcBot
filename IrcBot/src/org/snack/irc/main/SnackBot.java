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
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.UserListEvent;
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
	@Override
	public void onMessage(MessageEvent event) throws Exception {
		Chan chan = Configuration.CHANNELS.get(event.getChannel().getName());
		// Call for weather
		if (event.getMessage().startsWith(",we ") || event.getMessage().equals(",we") || event.getMessage().startsWith(".we ") || event.getMessage().equals(".we")
				|| event.getMessage().startsWith("!we ") || event.getMessage().equals("!we")) {
			if (chan.getWeather()) {
				WeatherHandler.getWeather(event);
			}

			// Call for now playing
		} else if (event.getMessage().startsWith(",np ") || event.getMessage().equals(",np") || event.getMessage().startsWith(".np ") || event.getMessage().equals(".np")
				|| event.getMessage().startsWith("!np ") || event.getMessage().equals("!np")) {
			if (chan.getLastfm()) {
				LastfmHandler.getLastfm(event);
			}

			// TODO: DOCS
		} else if (event.getMessage().startsWith(",quote ") || event.getMessage().equals(",quote") || event.getMessage().startsWith(".quote ")
				|| event.getMessage().equals(".quote") || event.getMessage().startsWith("!quote ") || event.getMessage().equals("!quote")) {
			if (chan.getQuote()) {
				QuoteHandler.getQuote(event);
			}

			// Call for HTML Title
		} else if (event.getMessage().contains("http://") || event.getMessage().contains("https://")) {
			if (chan.getHtml()) {
				HtmlHandler.getHTMLTitle(event);
			}

			// TODO: DOCS
		} else if (event.getMessage().startsWith(",tell ") || event.getMessage().startsWith(".tell ") || event.getMessage().startsWith("!tell ")) {
			if (chan.getTell()) {
				TellHandler.addTell(event);
			}

			// TODO: DOCS
		} else if (event.getMessage().startsWith(",help ") || event.getMessage().startsWith(".help ") || event.getMessage().startsWith("!help ")) {
			sendHelp(event);

			// } else if (event.getMessage().contains(",dis")) {
			// event.getBot().disconnect();
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

	@Override
	public void onJoin(JoinEvent event) throws Exception {
		testFunctions(Configuration.CHANNELS.get(event.getChannel().getName()), event.getUser().getNick(), 0);
		TellHandler.tell(event);
	}

	@Override
	public void onPart(PartEvent event) {
		testFunctions(Configuration.CHANNELS.get(event.getChannel().getName()), event.getUser().getNick(), 1);
	}

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

	// TODO: DOCS
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

	// TODO: DOCS
	private void sendHelp(MessageEvent event) {
		Chan chan = Configuration.CHANNELS.get(event.getChannel().getName());
		event.getBot().sendMessage(event.getUser(), "My commands:");
		if (chan.getWeather()) {
			event.getBot().sendNotice(event.getUser(), "Get weather: .we/,we/!we [name] (Name gets stored)");
		}
		if (chan.getLastfm()) {
			event.getBot().sendNotice(event.getUser(), "Get lastfm: .np/,np/!np [name] (Name gets stored)");
		}
		if (chan.getHtml()) {
			event.getBot().sendNotice(event.getUser(), "Auto respond to http(s):// links");
		}
		if (chan.getQuote()) {
			event.getBot().sendNotice(event.getUser(), "Quotes: .quote/,quote/!quote [name] (Name is optional)");
		}
		if (chan.getTell()) {
			event.getBot().sendNotice(event.getUser(), "Tell someone on join: .tell/,tell/!tell [message]");
		}
	}
}