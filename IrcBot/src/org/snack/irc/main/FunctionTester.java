package org.snack.irc.main;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class FunctionTester implements Runnable {

	private final Event<?> event;
	private final int joinpart;

	public FunctionTester(Event<?> event, int joinpart) {
		this.event = event;
		this.joinpart = joinpart;
	}

	@Override
	public void run() {
		prepareTest(event, joinpart);
	}

	/**
	 * Prepares testing all users on all channels
	 * 
	 * @param event
	 *            The event to get the user/bot from
	 * @param ev
	 *            The join or part variable
	 */
	private void prepareTest(Event<?> event, int ev) {
		for (Channel chan : event.getBot().getChannels()) {
			Chan ch = Config.channels.get(chan.getName());
			for (User user : event.getBot().getUsers(chan)) {
				for (Bot b : ch.bots) {
					if (b.name.equalsIgnoreCase(user.getNick())) {
						testFunctions(Config.channels.get(chan.getName()), b.name, ev);
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
		System.out.println(((event == 0) ? "Join: " : "Part: ") + chan.name + " " + nick);
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
				System.out.println("Functions: html:" + chan.getHtml() + " lastfm:" + chan.getLastfm() + " weather:" + chan.getWeather() + " quote:" + chan.getQuote() + " tell:"
						+ chan.getTell() + " translate:" + chan.getTranslate() + " romaji:" + chan.getRomaji());
			}
		}
	}
}
