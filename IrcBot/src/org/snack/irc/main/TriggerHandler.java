package org.snack.irc.main;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.Chan;

public abstract class TriggerHandler implements Runnable {

	public abstract boolean trigger(MessageEvent<?> event);

	public abstract boolean permission(Chan chan);

	public abstract void attachEvent(MessageEvent<?> event);

	@Override
	public abstract void run();
}
