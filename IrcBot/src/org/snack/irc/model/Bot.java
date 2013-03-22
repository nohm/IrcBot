package org.snack.irc.model;

/**
 * Stores all the bot settings this bot needs to keep track of
 * 
 * @author snack
 * 
 */
public class Bot {
	private final String name;
	private final boolean html;
	private final boolean lastfm;
	private final boolean weather;
	private final boolean quote;
	private final boolean tell;

	public Bot(String name, boolean html, boolean lastfm, boolean weather, boolean quote, boolean tell) {
		this.name = name;
		this.html = html;
		this.lastfm = lastfm;
		this.weather = weather;
		this.quote = quote;
		this.tell = tell;
	}

	public String getName() {
		return name;
	}

	public boolean getHtml() {
		return html;
	}

	public boolean getLastfm() {
		return lastfm;
	}

	public boolean getWeather() {
		return weather;
	}

	public boolean getQuote() {
		return quote;
	}

	public boolean getTell() {
		return tell;
	}
}
