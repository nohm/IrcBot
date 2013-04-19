package irc.handler.message;

import irc.database.DatabaseManager;
import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.model.LastfmUser;
import irc.settings.Config;
import irc.worker.LastfmAPI;

import org.pircbotx.hooks.events.MessageEvent;

public class Lastfm extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		DatabaseManager db = DatabaseManager.getInstance();
		LastfmUser user = db.getLastfmUser(event.getUser().getNick());
		String username = (event.getMessage().length() == 3) ? user.getUsername() : event.getMessage().split("np ")[1];
		username = (event.getMessage().split(" ").length == 2 && event.getMessage().split(" ")[1].startsWith("@")) ? db.getLastfmUser(event.getMessage().split(" ")[1].substring(1)).getUsername() : username;
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

		if (!username.equals("") && event.getMessage().split(" ").length == 2 && !event.getMessage().split(" ")[1].startsWith("@")) {
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
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() == 3 && event.getMessage().substring(1, 3).equals("np")) || event.getMessage().length() >= 5 && event.getMessage().substring(1, 4).equals("np "));
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
