package org.snack.irc.main;

import java.util.HashMap;
import java.util.Map.Entry;

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
					if (b.name.equalsIgnoreCase(user.getNick()) && b.enabled) {
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
		String toPrint = "~INFO " + type + chan.name + " Functions: ";
		for (Entry<String, Boolean> entry : chan.functions.entrySet()) {
			toPrint += entry.getKey() + ":" + entry.getValue() + " ";
		}
		Startup.print(toPrint);
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
		HashMap<String, Boolean> chan_func = chan.functions;
		HashMap<String, Boolean> chan_def_func = chan.def_func;
		for (Bot bot : chan.bots) {
			if (bot.name.equals(nick) && bot.enabled) {
				boolean event = (eventType == EventType.JOIN || eventType == EventType.USERLIST);
				HashMap<String, Boolean> bot_func = bot.functions;
				for (String key : bot_func.keySet()) {
					if (bot_func.get(key) && chan_def_func.get(key)) {
						chan_func.put(key, (event) ? false : true);
					}
				}
			}
		}
	}
}
