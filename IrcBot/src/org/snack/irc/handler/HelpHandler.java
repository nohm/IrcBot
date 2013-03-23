package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class HelpHandler {

	// TODO: DOCS
	public static void sendHelp(MessageEvent<?> event) {
		Chan chan = Config.channels.get(event.getChannel().getName());
		event.getBot().sendMessage(event.getUser(), "My commands:");
		if (chan.getWeather()) {
			event.getBot().sendNotice(event.getUser(), "Get weather: .we/,we/!we [name] (Name gets stored)");
		}
		if (chan.getLastfm()) {
			event.getBot().sendNotice(event.getUser(), "Get lastfm: .np/,np/!np [name] (Name gets stored)");
		}
		if (chan.getHtml()) {
			event.getBot().sendNotice(event.getUser(), "Auto respond to http(s):// links");
		}
		if (chan.getQuote()) {
			event.getBot().sendNotice(event.getUser(), "Quotes: .quote/,quote/!quote [name] (Name is optional)");
		}
		if (chan.getTell()) {
			event.getBot().sendNotice(event.getUser(), "Tell someone on join: .tell/,tell/!tell [message]");
		}
	}
}
