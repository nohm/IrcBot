package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.model.Quote;
import org.snack.irc.settings.Config;

public class QuoteHandler implements Runnable {

	private final MessageEvent<?> event;

	public QuoteHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		getQuote();
	}

	/**
	 * Gets a quote form the database and returns it, based on the command
	 * length it'll do a name or random search
	 */
	private void getQuote() {
		DatabaseManager db = DatabaseManager.getInstance();

		String response = "";
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
		System.out.println(quote.getChannel() + " <" + quote.getName() + "> " + quote.getMessage());
		event.getBot().sendMessage(event.getChannel(), response);
	}
}
