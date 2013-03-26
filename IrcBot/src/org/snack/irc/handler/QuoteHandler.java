package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.model.Quote;
import org.snack.irc.settings.Config;

public class QuoteHandler {

	public static void getQuote(MessageEvent<?> event) {
		DatabaseManager db = DatabaseManager.getInstance();

		if (event.getMessage().length() == 6) {
			Quote quote = db.getRandomQuote(event.getChannel().getName());
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("QU_SUC").replace("<name>", quote.getName()).replace("<quote>", quote.getMessage()));
		} else {
			String user = event.getMessage().split(" ")[1];
			Quote quote = db.getQuoteByName(event.getChannel().getName(), user);
			System.out.println(quote.getChannel() + " <" + quote.getName() + "> " + quote.getMessage());
			if (quote.getName().equals(user)) {
				event.getBot().sendMessage(event.getChannel(), Config.speech.get("QU_SUC").replace("<name>", quote.getName()).replace("<quote>", quote.getMessage()));
			} else {
				event.getBot().sendMessage(event.getChannel(), Config.speech.get("QU_ERR"));
			}
		}
	}
}
