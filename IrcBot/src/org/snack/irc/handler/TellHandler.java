package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.Config;

public class TellHandler {

	public static void add(MessageEvent<?> event) {
		DatabaseManager db = DatabaseManager.getInstance();
		String nick = event.getMessage().split(" ")[1];

		boolean online = false;
		for (User u : event.getChannel().getUsers()) {
			if (u.getNick().equalsIgnoreCase(nick)) {
				online = true;
			}
		}

		if (!online) {
			db.putTell(new Tell(event.getUser().getNick(), nick, event.getMessage().split("tell " + event.getUser().getNick())[1]));

			event.getBot().sendMessage(event.getChannel(), Config.speech.get("TE_ADD").replace("<name>", nick));
		} else {
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("TE_ERR").replace("<name>", nick));
		}
	}

	public static void tell(JoinEvent<?> event) {
		DatabaseManager db = DatabaseManager.getInstance();

		ArrayList<Tell> tells = db.getTells(event.getUser().getNick());
		for (Tell tell : tells) {
			if (tell.getName().equalsIgnoreCase(event.getUser().getNick())) {
				event.getBot().sendNotice(event.getUser(), Config.speech.get("TE_TEL").replace("<sender>", tell.getSender()).replace("<message>", tell.getMessage()));
				db.removeTell(tell);
			}
		}
	}
}
