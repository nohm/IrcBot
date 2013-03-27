package org.snack.irc.main;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class FunctionTester implements Runnable {

	private final Event<?> event;
	private final Channel channel;
	private final int joinpart;

	public FunctionTester(Event<?> event, Channel channel, int joinpart) {
		this.event = event;
		this.joinpart = joinpart;
		this.channel = channel;
	}

	@Override
	public void run() {
		prepareTestOne(event, channel, joinpart);
	}

	/**
	 * Tests one channel
	 * 
	 * @param channel
	 *            The channel to test
	 * @param ev
	 *            The join or part variable
	 */
	private void prepareTestOne(Event<?> event, Channel channel, int ev) {
		Chan chan = Config.channels.get(channel.getName());
		for (User user : event.getBot().getUsers(channel)) {
			for (Bot b : chan.bots) {
				if (b.name.equalsIgnoreCase(user.getNick())) {
					testFunctions(Config.channels.get(channel.getName()), b.name, ev);
				}
			}
		}
		Monitor.print("~INFO " + ((joinpart == 0) ? "Join: " : "Part: ") + chan.name + " Functions: html:" + chan.getHtml() + " lastfm:" + chan.getLastfm() + " weather:"
				+ chan.getWeather() + " quote:" + chan.getQuote() + " tell:" + chan.getTell() + " translate:" + chan.getTranslate() + " romaji:" + chan.getRomaji());
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
		for (Bot bot : chan.bots) {
			if (bot.name.equals(nick)) {
				if (bot.html && chan.func_html) {
					chan.setHtml((event == 0) ? false : true);
				}
				if (bot.lastfm && chan.func_lastfm) {
					chan.setLastfm((event == 0) ? false : true);
				}
				if (bot.weather && chan.func_weather) {
					chan.setWeather((event == 0) ? false : true);
				}
				if (bot.quote && chan.func_quote) {
					chan.setQuote((event == 0) ? false : true);
				}
				if (bot.tell && chan.func_tell) {
					chan.setTell((event == 0) ? false : true);
				}
				if (bot.translate && chan.func_translate) {
					chan.setTranslate((event == 0) ? false : true);
				}
				if (bot.romaji && chan.func_romaji) {
					chan.setRomaji((event == 0) ? false : true);
				}
			}
		}
	}
}
