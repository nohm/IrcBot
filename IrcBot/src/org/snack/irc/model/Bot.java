package org.snack.irc.model;

/**
 * Stores all the bot settings this bot needs to keep track of
 * 
 * @author snack
 * 
 */
public class Bot {
	public final String name;
	public final boolean html;
	public final boolean lastfm;
	public final boolean weather;
	public final boolean quote;
	public final boolean tell;
	public final boolean translate;
	public final boolean romaji;

	public Bot(String name, boolean html, boolean lastfm, boolean weather, boolean quote, boolean tell, boolean translate, boolean romaji) {
		this.name = name;
		this.html = html;
		this.lastfm = lastfm;
		this.weather = weather;
		this.quote = quote;
		this.tell = tell;
		this.translate = translate;
		this.romaji = romaji;
	}
}
