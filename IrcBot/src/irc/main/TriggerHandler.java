package irc.main;

import irc.model.Chan;

import org.pircbotx.hooks.events.MessageEvent;

/**
 * Wrapper for plugin modules, extend it for proper integration
 * 
 * @author snack
 *
 */
public abstract class TriggerHandler implements Runnable {

	/**
	 * Default runnable run() method
	 */
	@Override
	public abstract void run();

	/**
	 * Does your message trigger this plugin?
	 * 
	 * @param event, event has the data to check
	 * @return if it gets triggered
	 */
	public abstract boolean trigger(MessageEvent<?> event);

	/**
	 * Do you have permission to execute this?
	 * 
	 * @param chan, the channel to check the permissions from
	 * @return if you have permission
	 */
	public abstract boolean permission(Chan chan);

	/**
	 * Attaches the event to the runnable (it's a setter)
	 * 
	 * @param event, the event to attach
	 */
	public abstract void attachEvent(MessageEvent<?> event);
}
