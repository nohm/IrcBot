package org.snack.irc.handler;

import java.util.ArrayList;
import java.util.Random;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.settings.SettingParser;

public class QuoteHandler {

	public static void getQuote(MessageEvent<?> event) {
		ArrayList<String> storage;
		try {
			storage = SettingParser.parseQuotes();
		} catch (Exception e) {
			// e.printStackTrace();
			storage = new ArrayList<String>();
		}

		String quote = "";

		if (event.getMessage().equalsIgnoreCase(",quote") || event.getMessage().equalsIgnoreCase(".quote") || event.getMessage().equalsIgnoreCase("!quote")) {
			quote = storage.get(new Random().nextInt(storage.size() - 1));
			event.getBot().sendMessage(event.getChannel(), quote);
		} else {
			String name = "";
			String user = "<" + event.getMessage().split(" ")[1] + ">";
			int index = 0;
			while (!name.equalsIgnoreCase(user)) {
				if (index == storage.size() - 1) {
					break;
				}
				quote = storage.get(new Random().nextInt(storage.size() - 1));
				name = quote.split(" ")[0];
				index++;
			}
			if (name.equals(user)) {
				event.getBot().sendMessage(event.getChannel(), quote);
			} else {
				event.getBot().sendMessage(event.getChannel(), "No quote found.");
			}
		}
	}
}
