package irc.handler.message;

import irc.database.DatabaseManager;
import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.model.Quote;
import irc.settings.Config;

import org.pircbotx.hooks.events.MessageEvent;

public class Quotes extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		DatabaseManager db = DatabaseManager.getInstance();

		String response;
		Quote quote;

		if (event.getMessage().length() == 6) {
			quote = db.getRandomQuote(event.getChannel().getName());
			response = Config.speech.get("QU_SUC").replace("<name>", quote.getName()).replace("<quote>", quote.getMessage());
		} else {
			String user = event.getMessage().split(" ")[1];
			quote = db.getQuoteByName(event.getChannel().getName(), user);
			if (quote.getName().equals(user)) { // Check for empty value
				response = Config.speech.get("QU_SUC").replace("<name>", quote.getName()).replace("<quote>", quote.getMessage());
			} else {
				response = Config.speech.get("QU_ERR");
			}
		}
		Startup.print("~RESPONSE  Quoted: " + quote.getChannel() + " <" + quote.getName() + "> " + quote.getMessage());
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() == 6 && event.getMessage().substring(1, 6).equals("quote")) || (event.getMessage().length() > 7 && event.getMessage().split(" ").length >= 1 && event.getMessage().substring(1, 7).equals("quote ")));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("quote")) {
			return chan.functions.get("quote");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
