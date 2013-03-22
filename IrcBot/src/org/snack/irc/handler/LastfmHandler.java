package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.LastfmUser;
import org.snack.irc.settings.SettingParser;
import org.snack.irc.settings.SettingStorer;
import org.snack.irc.worker.LastfmAPI;

public class LastfmHandler {

	/**
	 * Parses the event for the username, makes LastfmAPI return their now
	 * playing/ last played and stores the username.
	 * 
	 * @param event
	 */
	public static void getLastfm(MessageEvent<?> event) {
		System.out.println(event.getMessage());
		ArrayList<LastfmUser> storage;
		try {
			storage = SettingParser.parseLUsers();
		} catch (Exception e) {
			// e.printStackTrace();
			storage = new ArrayList<LastfmUser>();
		}

		int changeNum = -1;
		String username = "";

		if (event.getMessage().equalsIgnoreCase(",np") || event.getMessage().equalsIgnoreCase(".np") || event.getMessage().equalsIgnoreCase("!np")) {
			for (LastfmUser user : storage) {
				if (user.getName().equals(event.getUser().getNick())) {
					username = user.getUsername();
				}
			}
		} else {
			username = event.getMessage().split("np ")[1];
		}

		String data[] = LastfmAPI.getSong(username);
		if (data[0] == null || data[1] == null || data[2] == null || data[3] == null) {
			event.getBot().sendMessage(event.getChannel(), data[0]);
		} else {
			if (data[0].equals("true")) {
				event.getBot().sendMessage(event.getChannel(), username + " is now playing: " + data[2] + " by " + data[1] + " on " + data[3]);
			} else {
				event.getBot().sendMessage(event.getChannel(), username + " last played: " + data[2] + " by " + data[1] + " on " + data[3]);
			}
		}

		for (int i = 0; i < (storage.size()); i++) {
			if (storage.get(i).getName().equals(event.getUser().getNick())) {
				changeNum = i;
			}
		}
		if (username != "") {
			if (changeNum == -1) {
				storage.add(new LastfmUser(event.getUser().getNick(), username));
			} else {
				storage.set(changeNum, new LastfmUser(event.getUser().getNick(), username));
			}

			try {
				SettingStorer.storeLUsers(storage);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
	}
}
