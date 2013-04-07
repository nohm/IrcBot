package org.snack.irc.main;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.snack.irc.enums.EventType;
import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class FunctionTester implements Runnable {

	private final Event<?> event;
	private final Channel channel;
	private final User user;
	private final EventType eventType;

	public FunctionTester(Event<?> event, Channel channel, User user, EventType eventType) {
		this.event = event;
		this.channel = channel;
		this.user = user;
		this.eventType = eventType;
	}

	@Override
	public void run() {
		if (channel != null) {
			prepareTestOne(channel);
		} else {
			prepareTestAll();
		}
	}

	/**
	 * Tests all the channels
	 */
	private void prepareTestAll() {
		for (Channel channel : event.getBot().getChannels()) {
			prepareTestOne(channel);
		}
	}

	/**
	 * Tests one channel
	 * 
	 * @param channel
	 *            The channel to test
	 * @param ev
	 *            The join or part variable
	 */
	private void prepareTestOne(Channel channel) {
		Chan chan = Config.channels.get(channel.getName());
		if (eventType == EventType.USERLIST) {
			for (User user : event.getBot().getUsers(channel)) {
				for (Bot b : chan.bots) {
					if (b.name.equalsIgnoreCase(user.getNick())) {
						testFunctions(chan, user.getNick());
					}
				}
			}
		} else {
			testFunctions(chan, user.getNick());
		}
		printFunctions(chan);
	}

	private void printFunctions(Chan chan) {
		String type;
		switch (eventType) {
		case JOIN:
			type = "Join: ";
			break;
		case PART:
			type = "Part: ";
			break;
		case QUIT:
			type = "Quit: ";
			break;
		case USERLIST:
			type = "Userlist: ";
			break;
		default:
			type = "Join: ";
		}
		Monitor.print("~INFO " + type + chan.name + " Functions: html:" + chan.getHtml() + " lastfm:" + chan.getLastfm() + " weather:" + chan.getWeather() + " quote:"
				+ chan.getQuote() + " tell:" + chan.getTell() + " translate:" + chan.getTranslate() + " romaji:" + chan.getRomaji() + " wiki:" + chan.getWiki() + " search:"
				+ chan.getSearch());
	}

	/**
	 * Tests whether each function should be on or off per channel
	 * 
	 * @param chan
	 *            The channel in question
	 * @param nick
	 *            The nick of the user that joined/parted/left
	 */
	private void testFunctions(Chan chan, String nick) {
		for (Bot bot : chan.bots) {
			if (bot.name.equals(nick)) {
				boolean event = (eventType == EventType.JOIN || eventType == EventType.USERLIST);
				if (bot.html && chan.func_html) {
					chan.setHtml((event) ? false : true);
				}
				if (bot.lastfm && chan.func_lastfm) {
					chan.setLastfm((event) ? false : true);
				}
				if (bot.weather && chan.func_weather) {
					chan.setWeather((event) ? false : true);
				}
				if (bot.quote && chan.func_quote) {
					chan.setQuote((event) ? false : true);
				}
				if (bot.tell && chan.func_tell) {
					chan.setTell((event) ? false : true);
				}
				if (bot.translate && chan.func_translate) {
					chan.setTranslate((event) ? false : true);
				}
				if (bot.romaji && chan.func_romaji) {
					chan.setRomaji((event) ? false : true);
				}
				if (bot.wiki && chan.func_wiki) {
					chan.setWiki((event) ? false : true);
				}
				if (bot.search && chan.func_search) {
					chan.setSearch((event) ? false : true);
				}
			}
		}
	}
}
