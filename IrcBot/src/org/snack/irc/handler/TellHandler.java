package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.Config;
import org.snack.irc.settings.SettingParser;
import org.snack.irc.settings.SettingStorer;

public class TellHandler {

	public static void add(MessageEvent<?> event) {
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

		event.getBot().sendMessage(event.getChannel(), Config.speech.get("TE_ADD").replace("<name>", event.getMessage().split(" ")[1]));
	}

	public static void tell(JoinEvent<?> event) {
		ArrayList<Tell> storage;
		try {
			storage = SettingParser.parseTells();
		} catch (Exception e) {
			// e.printStackTrace();
			storage = new ArrayList<Tell>();
		}

		ArrayList<Tell> newStorage = new ArrayList<Tell>();
		for (Tell tell : storage) {
			if (tell.getName().equalsIgnoreCase(event.getUser().getNick())) {
				event.getBot().sendNotice(event.getUser(), Config.speech.get("TE_TEL").replace("<sender>", tell.getSender()).replace("<message>", tell.getMessage()));
			} else {
				newStorage.add(tell);
			}
		}

		try {
			SettingStorer.storeTells(newStorage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
