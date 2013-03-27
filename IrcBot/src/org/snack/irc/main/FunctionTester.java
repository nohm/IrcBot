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
	private final EventType eventType;

	public FunctionTester(Event<?> event, Channel channel, EventType eventType) {
		this.event = event;
		this.channel = channel;
		this.eventType = eventType;
	}

	@Override
	public void run() {
		if (channel != null) {
			prepareTestOne(this.channel);
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
		for (User user : event.getBot().getUsers(channel)) {
			for (Bot b : chan.bots) {
				if (b.name.equalsIgnoreCase(user.getNick())) {
					testFunctions(Config.channels.get(channel.getName()), b.name);
				}
			}
		}
		String type;
		switch (eventType) {
		case JOIN:
			type = "Join: ";
		case PART:
			type = "Part: ";
		case QUIT:
			type = "Quit: ";
		case USERLIST:
			type = "Userlist: ";
		default:
			type = "Join: ";
		}
		Monitor.print("~INFO " + type + chan.name + " Functions: html:" + chan.getHtml() + " lastfm:" + chan.getLastfm() + " weather:" + chan.getWeather() + " quote:"
				+ chan.getQuote() + " tell:" + chan.getTell() + " translate:" + chan.getTranslate() + " romaji:" + chan.getRomaji());
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
	private void testFunctions(Chan chan, String nick) {
		for (Bot bot : chan.bots) {
			if (bot.name.equals(nick)) {
				if (bot.html && chan.func_html) {
					chan.setHtml((eventType == EventType.JOIN) ? false : true);
				}
				if (bot.lastfm && chan.func_lastfm) {
					chan.setLastfm((eventType == EventType.JOIN) ? false : true);
				}
				if (bot.weather && chan.func_weather) {
					chan.setWeather((eventType == EventType.JOIN) ? false : true);
				}
				if (bot.quote && chan.func_quote) {
					chan.setQuote((eventType == EventType.JOIN) ? false : true);
				}
				if (bot.tell && chan.func_tell) {
					chan.setTell((eventType == EventType.JOIN) ? false : true);
				}
				if (bot.translate && chan.func_translate) {
					chan.setTranslate((eventType == EventType.JOIN) ? false : true);
				}
				if (bot.romaji && chan.func_romaji) {
					chan.setRomaji((eventType == EventType.JOIN) ? false : true);
				}
			}
		}
	}
}
