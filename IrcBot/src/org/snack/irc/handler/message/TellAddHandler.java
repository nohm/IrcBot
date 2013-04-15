package org.snack.irc.handler.message;

import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.Config;

public class TellAddHandler extends TriggerHandler implements Runnable {

	private MessageEvent<?> event;

	public TellAddHandler() {}

	public TellAddHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		add();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 6 && event.getMessage().substring(1, 6).equals("tell "));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	/**
	 * Adds a new tell to the database
	 */
	private void add() {
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
			Monitor.print("~RESPONSE  Added tell: " + tell.getSender() + " > " + tell.getName() + " : " + tell.getMessage());
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("TE_ADD").replace("<name>", nick));
		} else {
			Monitor.print("~RESPONSE  Error adding tell.");
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("TE_ERR").replace("<name>", nick));
		}
	}
}
