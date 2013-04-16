package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Startup;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.model.LastfmUser;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.LastfmAPI;

public class Lastfm extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		DatabaseManager db = DatabaseManager.getInstance();
		LastfmUser user = db.getLastfmUser(event.getUser().getNick());
		String username = (event.getMessage().length() == 3) ? user.getUsername() : event.getMessage().split("np ")[1];
		username = (username.equals("")) ? event.getUser().getNick() : username;

		String response;
		String data[] = LastfmAPI.getSong(username);
		if (data == null) {
			response = Config.speech.get("LA_ERR");
		} else {
			if (data[0].equals("true")) {
				response = Config.speech.get("LA_SUC_NP").replace("<username>", username).replace("<song>", data[2]).replace("<artist>", data[1]).replace("<album>", data[3]);
			} else {
				response = Config.speech.get("LA_SUC_LP").replace("<username>", username).replace("<song>", data[2]).replace("<artist>", data[1]).replace("<album>", data[3]);
			}
		}
		Startup.print("~RESPONSE  " + response);
		event.getBot().sendMessage(event.getChannel(), response);

		if (!username.equals("")) {
			if (user.getName().equals("")) {
				Startup.print("~RESPONSE  Put lastfmuser: " + event.getUser().getNick() + " " + username);
				db.putLastfmUser(new LastfmUser(event.getUser().getNick(), username));
			} else if (!user.getUsername().equalsIgnoreCase(username)) {
				Startup.print("~RESPONSE  Updated lastfmuser: " + user.getName() + " " + username);
				db.updateLastfmUser(new LastfmUser(user.getName(), username));
			}
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 3 && event.getMessage().substring(1, 3).equals("np"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("lastfm")) {
			return chan.functions.get("lastfm");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
