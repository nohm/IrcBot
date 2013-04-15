package org.snack.irc.handler.message;

import java.util.Random;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class EightBall extends TriggerHandler {

	private MessageEvent<?> event;

	public EightBall() {}

	public EightBall(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		askBall();
	}

	private void askBall() {
		String[] replies = {
				"It is certain",
				"It is decidedly so",
				"Without a doubt",
				"Yes, definitely",
				"You may rely on it",
				"As I see it, yes",
				"Most likely",
				"Outlook good",
				"Yes",
				"Signs point to yes",
				"Reply hazy, try again",
				"Ask again later",
				"Better not tell you now",
				"Cannot predict now",
				"Concentrate and ask again",
				"Don't count on it",
				"My reply is no",
				"My sources say no",
				"Outlook not so good",
				"Very doubtful"
		};

		String reply = replies[new Random().nextInt(replies.length - 1)];

		if (!event.getMessage().endsWith("?")) {
			reply = "Questions end with a question mark.";
		}

		Monitor.print("~INFO Response: " + reply);
		event.getBot().sendAction(event.getChannel(), "shakes the magic 8ball: " + reply);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 7 && event.getMessage().substring(1, 7).equals("8ball "));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("eightball")) {
			return chan.functions.get("eightball");
		} else {
			return true;
		}
	}
}
