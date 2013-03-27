package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Monitor;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.Config;

public class TellHandler implements Runnable {

	private final MessageEvent<?> mEvent;
	private final JoinEvent<?> jEvent;
	private final boolean add;

	public TellHandler(MessageEvent<?> mEvent, JoinEvent<?> jEvent, boolean add) {
		this.mEvent = mEvent;
		this.jEvent = jEvent;
		this.add = add;
	}

	@Override
	public void run() {
		if (add) {
			add();
		} else {
			tell();
		}
	}

	/**
	 * Adds a new tell to the database
	 */
	private void add() {
		DatabaseManager db = DatabaseManager.getInstance();
		String nick = mEvent.getMessage().split(" ")[1];

		boolean online = false;
		for (User u : mEvent.getChannel().getUsers()) {
			if (u.getNick().equalsIgnoreCase(nick)) {
				online = true;
			}
		}

		if (!online) {
			Tell tell = new Tell(mEvent.getUser().getNick(), nick, mEvent.getMessage().split("tell " + mEvent.getUser().getNick())[1]);
			db.putTell(tell);
			Monitor.print("~RESPONSE  Added tell: " + tell.getSender() + " > " + tell.getName() + " : " + tell.getMessage());
			mEvent.getBot().sendMessage(mEvent.getChannel(), Config.speech.get("TE_ADD").replace("<name>", nick));
		} else {
			Monitor.print("~RESPONSE  Error adding tell.");
			mEvent.getBot().sendMessage(mEvent.getChannel(), Config.speech.get("TE_ERR").replace("<name>", nick));
		}
	}

	/**
	 * Tells all the tells it has to tell.
	 */
	private void tell() {
		DatabaseManager db = DatabaseManager.getInstance();

		ArrayList<Tell> tells = db.getTells(jEvent.getUser().getNick());
		for (Tell tell : tells) {
			if (tell.getName().equalsIgnoreCase(jEvent.getUser().getNick())) {
				String response = Config.speech.get("TE_TEL").replace("<sender>", tell.getSender()).replace("<message>", tell.getMessage());
				Monitor.print("~RESPONSE  Told: " + response);
				jEvent.getBot().sendNotice(jEvent.getUser(), response);
				db.removeTell(tell);
			}
		}
	}
}
