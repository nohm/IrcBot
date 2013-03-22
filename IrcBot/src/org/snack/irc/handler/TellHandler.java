package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.SettingParser;
import org.snack.irc.settings.SettingStorer;

public class TellHandler {

	public static void addTell(MessageEvent<?> event) {
		ArrayList<Tell> storage;
		try {
			storage = SettingParser.parseTells();
		} catch (Exception e) {
			storage = new ArrayList<Tell>();
		}

		storage.add(new Tell(event.getMessage().split(" ")[1], event.getUser().getNick(), event.getMessage().split("tell " + event.getUser().getNick())[1]));

		try {
			SettingStorer.storeTells(storage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		event.getBot().sendMessage(event.getChannel(), "I'll tell that to " + event.getMessage().split(" ")[1] + " for you.");
	}
}
