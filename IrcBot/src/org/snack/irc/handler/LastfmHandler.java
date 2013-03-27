package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.model.LastfmUser;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.LastfmAPI;

public class LastfmHandler implements Runnable {

	private final MessageEvent<?> event;

	public LastfmHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		getLastfm();
	}

	/**
	 * Parses the event for the username, makes LastfmAPI return their now
	 * playing/ last played and stores the username.
	 * 
	 * @param event
	 */
	private void getLastfm() {
		DatabaseManager db = DatabaseManager.getInstance();
		String username = "";
		LastfmUser user = db.getLastfmUser(event.getUser().getNick());

		if (event.getMessage().length() == 3) {
			username = user.getUsername();
			if (username.equals("")) {
				username = event.getUser().getNick();
			}
		} else {
			username = event.getMessage().split("np ")[1];
		}

		String data[] = LastfmAPI.getSong(username);
		if (data == null) {
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("LA_ERR"));
		} else {
			if (data[0].equals("true")) {
				event.getBot().sendMessage(event.getChannel(),
						Config.speech.get("LA_SUC_NP").replace("<username>", username).replace("<song>", data[2]).replace("<artist>", data[1]).replace("<album>", data[3]));
			} else {
				event.getBot().sendMessage(event.getChannel(),
						Config.speech.get("LA_SUC_LP").replace("<username>", username).replace("<song>", data[2]).replace("<artist>", data[1]).replace("<album>", data[3]));
			}
		}

		if (!username.equals("")) {
			if (user.getName().equals("")) {
				db.putLastfmUser(new LastfmUser(event.getUser().getNick(), username));
			} else if (!user.getUsername().equalsIgnoreCase(username)) {
				db.updateLastfmUser(new LastfmUser(user.getName(), username));
			}
		}
	}
}
