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
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.enums.EventType;
import org.snack.irc.enums.QuoteType;
import org.snack.irc.enums.RomajiType;
import org.snack.irc.enums.TellType;
import org.snack.irc.handler.GreetHandler;
import org.snack.irc.handler.HelpHandler;
import org.snack.irc.handler.HtmlHandler;
import org.snack.irc.handler.LastfmHandler;
import org.snack.irc.handler.QuoteHandler;
import org.snack.irc.handler.RomajiHandler;
import org.snack.irc.handler.SearchHandler;
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

	private static PircBotX bot;

	public BotListener(PircBotX bot) {
		BotListener.bot = bot;
	}

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
		Monitor.print("<<MSG " + chan.name + " " + nick + ": " + message);

		// Command handler
		if (Config.sett_str.get("IDENTIFIERS").contains(message.substring(0, 1))) {

			// Call for weather
			if (message.substring(1, 3).equals("we")) {
				if (chan.getWeather() && !chan.getMute()) {
					Monitor.print("~COMMAND Weather: " + nick);
					new WeatherHandler(event).run();
				}

				// Call for now playing
			} else if (message.substring(1, 3).equals("np")) {
				if (chan.getLastfm() && !chan.getMute()) {
					Monitor.print("~COMMAND Lastfm: " + nick);
					new LastfmHandler(event).run();
				}

				// Cal for help
			} else if (message.substring(1, 5).equals("help")) {
				if (!chan.getMute()) {
					Monitor.print("~COMMAND Help: " + nick);
					new HelpHandler(event).run();
				}

				// Call for quotes
			} else if (message.substring(1, 6).equals("quote")) {
				if (chan.getQuote() && !chan.getMute()) {
					Monitor.print("~COMMAND Quote: " + nick);
					new QuoteHandler(event, (message.split(" ")[1].equals("add") && message.split(" ").length >= 4) ? QuoteType.ADD : QuoteType.QUOTE).run();
				}

				// Call for tell
			} else if (message.substring(1, 6).equals("tell ")) {
				if (chan.getTell() && !chan.getMute()) {
					Monitor.print("~COMMAND Tell: " + nick);
					new TellHandler(event, null, TellType.ADD).run();
				}

				// Call for search
			} else if (message.substring(1, 7).equals("search")) {
				if (chan.getSearch() && !chan.getMute()) {
					Monitor.print("~COMMAND Search: " + nick);
					new SearchHandler(event).run();
				}

				// Call for romaji
			} else if (message.substring(1, 8).equals("romaji ")) {
				if (chan.getRomaji() && !chan.getMute()) {
					Monitor.print("~COMMAND Romaji: " + nick);
					new RomajiHandler(event, RomajiType.ROMAJI).run();
				}

				// Call for katakana
			} else if (message.substring(1, 10).equals("katakana ")) {
				if (chan.getRomaji() && !chan.getMute()) {
					Monitor.print("~COMMAND Katakana: " + nick);
					new RomajiHandler(event, RomajiType.KATAKANA).run();
				}

				// Call for translate
			} else if (message.substring(1, 11).equals("translate ")) {
				if (chan.getTranslate() && !chan.getMute()) {
					Monitor.print("~COMMAND Translate: " + nick);
					new TranslateHandler(event).run();
				}
			}

			// Call for HTML Title
		} else if (message.contains("http://") || message.contains("https://")) {
			if (chan.getHtml() && !chan.getMute()) {
				Monitor.print("~COMMAND Html: " + nick);
				new HtmlHandler(event).run();
			}

			// Admin commands
		} else if (message.startsWith(bot.getNick() + ":")) {
			if (user.getNick().equals(Config.sett_str.get("ADMIN"))) {
				if (message.equals(nick + ":mute") || message.equals(nick + ":unmute")) {
					chan.setMute((message.equals(nick + ":mute")) ? true : false);
					bot.sendMessage(channel, chan.getMute() ? Config.speech.get("MUTE") : Config.speech.get("UNMUTE"));
					Monitor.print("~INFO Muted: " + chan.getMute());
				} else if (message.equals(nick + ":restart")) {
					Startup.restart();
				}
			}

		} else {
			boolean add = true;
			for (Bot b : chan.bots) {
				if (b.name.equalsIgnoreCase(nick)) {
					add = false;
				}
			}
			if (Config.sett_str.get("BOT_NAME").equalsIgnoreCase(nick)) {
				add = false;
			}
			// Add random quotes
			if (new Random().nextInt(100) > 95 && add && message.length() <= Config.sett_int.get("QUOTE_MAX") && message.length() >= Config.sett_int.get("QUOTE_MIN")) {
				Monitor.print("~INFO Added quote: " + chan.name + " <" + user.getNick() + "> " + message);
				DatabaseManager.getInstance().putQuote(new Quote(chan.name, user.getNick(), message));
			}
		}
	}

	/**
	 * Auto-rejoin on kick.
	 */
	@Override
	public void onKick(KickEvent event) {
		Monitor.print("<<KICK " + event.getChannel().getName());
		event.getBot().sendRawLine("JOIN " + event.getChannel().getName());
		Monitor.print("~INFO Rejoined");
	}

	/**
	 * Called whenever someone (or the bot) joins, updates the functions.
	 */
	@Override
	public void onJoin(JoinEvent event) {
		if (!event.getUser().getNick().equals(Config.sett_str.get("BOT_NAME"))) {
			Monitor.print("<<JOIN " + event.getChannel().getName() + " " + event.getUser().getNick());
			new FunctionTester(event, event.getChannel(), event.getUser(), EventType.JOIN).run();
			Chan chan = Config.channels.get(event.getChannel().getName());
			if (chan.getGreet() && !chan.getMute()) {
				new GreetHandler(event).run();
			}
		}
		new TellHandler(null, event, TellType.TELL).run();
		Monitor.print("~INFO Cleaned tells: " + event.getChannel().getName());
	}

	/**
	 * Called whenever someone (or the bot) parts/leaves, updates the functions.
	 */
	@Override
	public void onPart(PartEvent event) {
		Monitor.print("<<PART " + event.getChannel().getName() + " " + event.getUser().getNick());
		new FunctionTester(event, event.getChannel(), event.getUser(), EventType.PART).run();
	}

	/**
	 * Called whenever someone (or the bot) quits, updates the functions. Checks
	 * all channels because the user can leave unlimited channels at once
	 */
	@Override
	public void onQuit(QuitEvent event) {
		Monitor.print("<<QUIT " + event.getUser().getNick());
		new FunctionTester(event, null, event.getUser(), EventType.QUIT).run();
	}

	/**
	 * Called when the bot has fully started, updates the functions.
	 */
	@Override
	public void onUserList(UserListEvent event) {
		Monitor.print("<<USERLIST");
		new FunctionTester(event, event.getChannel(), null, EventType.USERLIST).run();
	}

	/**
	 * Called when the connection gets lost, will retry to join every 5000ms
	 */
	@Override
	public void onDisconnect(DisconnectEvent event) throws Exception {
		Monitor.print("<<DISCONNECT");
		try {
			event.getBot().connect(Config.sett_str.get("SERVER"));
			if (!Config.sett_str.get("BOT_PASS").equals("") && Config.sett_str.get("BOT_PASS") != null) {
				event.getBot().sendRawLine("NICKSERV IDENTIFY " + Config.sett_str.get("BOT_PASS"));
			}
			for (Chan channel : Config.channels.values()) {
				event.getBot().sendRawLine("JOIN " + channel.name);
			}
			Monitor.print("~INFO Reconnected");
		} catch (Exception e) {
			Thread.sleep(5000);
			onDisconnect(event);
		}
	}

	public static void sendCustomMessage(String type, String target, String message) {
		if (type.equalsIgnoreCase("PRIVMSG") && (!target.equals("") || target == null)) {
			if (target.equals("#")) {
				Monitor.print(">>BROADCAST " + message);
				for (Channel channel : bot.getChannels()) {
					bot.sendMessage(channel, "Broadcast: " + message);
				}
			} else {
				Monitor.print(">>MSG " + target + ": " + message);
				bot.sendMessage(target, message);
			}
		} else if (type.equalsIgnoreCase("ACTION")) {
			Monitor.print(">>ACTION " + target + ": " + message);
			bot.sendAction(target, message);
		} else {
			Monitor.print(">>RAW " + type + " " + target + ": " + message);
			bot.sendRawLine(type + " " + target + " :" + message);
		}
	}
}