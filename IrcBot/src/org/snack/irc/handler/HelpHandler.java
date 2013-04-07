package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.Chan;
import org.snack.irc.settings.Config;

public class HelpHandler implements Runnable {

	private final MessageEvent<?> event;

	public HelpHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		sendHelp();
	}

	/**
	 * Returns help based on the channels enabled functions
	 */
	private void sendHelp() {
		Chan chan = Config.channels.get(event.getChannel().getName());
		event.getBot().sendNotice(event.getUser(), "My commands:");
		event.getBot().sendNotice(event.getUser(), "I respond to commands starting with: " + Config.sett_str.get("IDENTIFIERS"));
		if (chan.getWeather()) {
			event.getBot().sendNotice(event.getUser(), "Get weather: .we [name] (Name gets stored)");
		}
		if (chan.getLastfm()) {
			event.getBot().sendNotice(event.getUser(), "Get lastfm: .np [name] (Name gets stored)");
		}
		if (chan.getHtml()) {
			event.getBot().sendNotice(event.getUser(), "Auto respond to http(s):// links");
		}
		if (chan.getQuote()) {
			event.getBot().sendNotice(event.getUser(), "Quotes: .quote [add] (To add) [name] (Name is optional) [quote] (If adding)");
		}
		if (chan.getTell()) {
			event.getBot().sendNotice(event.getUser(), "Tell someone on join: .tell [message]");
		}
		if (chan.getTranslate()) {
			event.getBot().sendNotice(event.getUser(), "Translate to english: .translate [message]");
		}
		if (chan.getRomaji()) {
			event.getBot().sendNotice(event.getUser(), "Change romaji to katakana: .romaji [message]");
			event.getBot().sendNotice(event.getUser(), "Change katakana to romaji: .katakana [message]");
		}
		if (chan.getWiki()) {
			event.getBot().sendNotice(event.getUser(), "Search wikipedia: .wiki[-language] (Language is optional) [term]");
		}
		if (chan.getSearch()) {
			event.getBot().sendNotice(event.getUser(), "Search for threads: .search[-board] (Board is optional) [term]");
		}
	}
}
