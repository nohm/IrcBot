package org.snack.irc.model;

/**
 * Stores all the bot settings this bot needs to keep track of
 * 
 * @author snack
 * 
 */
public class Bot {
	public final String name;
	public final boolean greet;
	public final boolean html;
	public final boolean lastfm;
	public final boolean weather;
	public final boolean quote;
	public final boolean tell;
	public final boolean translate;
	public final boolean romaji;
	public final boolean wiki;
	public final boolean search;

	public Bot(String name, boolean greet, boolean html, boolean lastfm, boolean weather, boolean quote, boolean tell, boolean translate, boolean romaji, boolean wiki,
			boolean search) {
		this.greet = greet;
		this.name = name;
		this.html = html;
		this.lastfm = lastfm;
		this.weather = weather;
		this.quote = quote;
		this.tell = tell;
		this.translate = translate;
		this.romaji = romaji;
		this.wiki = wiki;
		this.search = search;
	}
}
