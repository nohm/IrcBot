package irc.handler.message;

import irc.database.DatabaseManager;
import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.model.LastMsg;
import irc.settings.Config;

import org.pircbotx.hooks.events.MessageEvent;

public class Seen extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;

		LastMsg msg = DatabaseManager.getInstance().getLastMsg(event.getMessage().split(" ")[1]);
		if (msg.getName().equals("")) {
			response = "I don't know " + event.getMessage().split(" ")[1];
		} else {
			long time = (System.currentTimeMillis() - msg.getTime());
			int minutes = (int) ((time / (1000*60)) % 60);
			int hours   = (int) ((time / (1000*60*60)) % 24);
			int days   = (int) ((time / (1000*60*60*24)) % 365);
			int years = (int)(time / 1000*60*60*24*365);
			response = "Last time I've seen " + event.getMessage().split(" ")[1] + " is ";
			if (years != 0) {
				response += years + " years, ";
			}
			if (days != 0) {
				response += days + " days, ";
			}
			if (hours != 0) {
				response += hours + " hours, ";
			}
			response += minutes + "minutes ago";
			if (!msg.getText().equals("")) {
				if (msg.getText().length() > 100) {
					response += " saying: " + msg.getText().substring(0, 96) + "...";
				} else {
					response += " saying: " + msg.getText();
				}
			}
		}

		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ").length == 2 && event.getMessage().split(" ")[0].endsWith("seen"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("seen")) {
			return chan.functions.get("seen");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

}
