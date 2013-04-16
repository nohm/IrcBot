package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Startup;
import org.snack.irc.model.Tell;
import org.snack.irc.settings.Config;

public class TellHandler implements Runnable {

	private MessageEvent<?> mEvent;
	private JoinEvent<?> jEvent;
	private NickChangeEvent<?> nEvent;

	public TellHandler() {}

	public TellHandler(MessageEvent<?> mEvent, JoinEvent<?> jEvent, NickChangeEvent<?> nEvent) {
		this.mEvent = mEvent;
		this.jEvent = jEvent;
		this.nEvent = nEvent;
	}

	@Override
	public void run() {
		if (jEvent == null) {
			if (nEvent == null) {
				tell(null, null, mEvent);
			} else {
				tell(null, nEvent, null);
			}
		} else {
			tell(jEvent, null, null);
		}
	}

	/**
	 * Tells all the tells it has to tell.
	 */
	private void tell(JoinEvent<?> j, NickChangeEvent<?> n, MessageEvent<?> m) {
		DatabaseManager db = DatabaseManager.getInstance();

		if (j == null) {
			if (n == null) {
				ArrayList<Tell> tells = db.getTells(mEvent.getUser().getNick());
				for (Tell tell : tells) {
					if (tell.getName().equalsIgnoreCase(nEvent.getUser().getNick())) {
						String response = Config.speech.get("TE_TEL").replace("<sender>", tell.getSender()).replace("<message>", tell.getMessage());
						Startup.print("~RESPONSE  Told: " + response);
						mEvent.getBot().sendNotice(mEvent.getUser(), response);
						db.removeTell(tell);
					}
				}
			} else {
				ArrayList<Tell> tells = db.getTells(nEvent.getUser().getNick());
				for (Tell tell : tells) {
					if (tell.getName().equalsIgnoreCase(nEvent.getUser().getNick())) {
						String response = Config.speech.get("TE_TEL").replace("<sender>", tell.getSender()).replace("<message>", tell.getMessage());
						Startup.print("~RESPONSE  Told: " + response);
						nEvent.getBot().sendNotice(nEvent.getUser(), response);
						db.removeTell(tell);
					}
				}
			}
		} else {
			ArrayList<Tell> tells = db.getTells(jEvent.getUser().getNick());
			for (Tell tell : tells) {
				if (tell.getName().equalsIgnoreCase(jEvent.getUser().getNick())) {
					String response = Config.speech.get("TE_TEL").replace("<sender>", tell.getSender()).replace("<message>", tell.getMessage());
					Startup.print("~RESPONSE  Told: " + response);
					jEvent.getBot().sendNotice(jEvent.getUser(), response);
					db.removeTell(tell);
				}
			}
		}
	}
}
