package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.enums.QuoteType;
import org.snack.irc.main.Monitor;
import org.snack.irc.model.Quote;
import org.snack.irc.settings.Config;

public class QuoteHandler implements Runnable {

	private final MessageEvent<?> event;
	private final QuoteType quoteType;

	public QuoteHandler(MessageEvent<?> event, QuoteType quoteType) {
		this.event = event;
		this.quoteType = quoteType;
	}

	@Override
	public void run() {
		if (quoteType == QuoteType.ADD) {
			addQuote();
		} else {
			getQuote();
		}
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
		Monitor.print("~RESPONSE  Quoted: " + quote.getChannel() + " <" + quote.getName() + "> " + quote.getMessage());
		event.getBot().sendMessage(event.getChannel(), response);
	}

	/**
	 * Add a new quote to the database
	 */
	private void addQuote() {
		DatabaseManager db = DatabaseManager.getInstance();

		String name = event.getMessage().split(" ")[2];
		String message = event.getMessage().substring(event.getMessage().indexOf(name) + name.length() + 1);

		Quote quote = new Quote(event.getChannel().getName(), name, message);
		db.putQuote(quote);
		Monitor.print("~RESPONSE  Added: " + quote.getChannel() + " <" + quote.getName() + "> " + quote.getMessage());
		event.getBot().sendMessage(event.getChannel(), Config.speech.get("QU_ADD"));
	}
}
