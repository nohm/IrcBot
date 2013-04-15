package org.snack.irc.main;

import org.pircbotx.hooks.events.MessageEvent;

public abstract class TriggerHandler implements Runnable {

	public abstract boolean trigger(MessageEvent<?> event);

	public abstract void attachEvent(MessageEvent<?> event);

	@Override
	public abstract void run();
}
