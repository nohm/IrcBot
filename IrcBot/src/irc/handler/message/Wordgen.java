package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.io.ByteArrayOutputStream;

import org.pircbotx.hooks.events.MessageEvent;

public class Wordgen extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response;
		try {
			ByteArrayOutputStream output = Utils.httpRequest("http://randomword.setgetgo.com/get.php");
			response = "Here you go: " + output.toString().substring(1);
		} catch(Exception e) {
			response = "Error generating :(";
		}
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().substring(1).equals("wordgen"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("wordgen")) {
			return chan.functions.get("wordgen");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}