package org.snack.irc.handler;

import java.util.Random;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;

public class EightBallHandler implements Runnable {

	private final MessageEvent<?> event;

	public EightBallHandler(MessageEvent<?> event) {
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
		event.getBot().sendMessage(event.getChannel(), "8-Ball says: " + reply);
	}
}
