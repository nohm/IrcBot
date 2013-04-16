package org.snack.irc.handler.message;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Startup;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.Config;

public class AddTell extends TriggerHandler implements Runnable {

	private MessageEvent<?> event;

	public AddTell() {}

	public AddTell(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		DatabaseManager db = DatabaseManager.getInstance();
		String nick = event.getMessage().split(" ")[1];

		boolean online = false;

		for (User u : event.getChannel().getUsers()) {
			if (u.getNick().equalsIgnoreCase(nick) && (System.currentTimeMillis() - db.getLastMsg(nick).getTime()) < (10 * 60 * 1000)) {
				online = true;
			}
		}

		if (!online) {
			Tell tell = new Tell(event.getUser().getNick(), nick, event.getMessage().split("tell " + nick)[1]);
			db.putTell(tell);
			Startup.print("~RESPONSE  Added tell: " + tell.getSender() + " > " + tell.getName() + " : " + tell.getMessage());
			event.getBot().sendNotice(event.getUser(), Config.speech.get("TE_ADD").replace("<name>", nick));
		} else {
			Startup.print("~RESPONSE  Error adding tell.");
			event.getBot().sendNotice(event.getUser(), Config.speech.get("TE_ERR").replace("<name>", nick));
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 6 && event.getMessage().substring(1, 6).equals("tell "));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("tell")) {
			return chan.functions.get("tell");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
