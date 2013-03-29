package org.snack.irc.model;

import java.util.ArrayList;

/**
 * Stores all the channel settings, also keeps defaults
 * 
 * @author snack
 * 
 */
public class Chan {
	// Name
	public final String name;
	// Functions that can be switched
	private boolean greet;
	public final boolean greet_visible;
	private boolean html;
	private boolean lastfm;
	private boolean weather;
	private boolean quote;
	private boolean tell;
	private boolean translate;
	private boolean romaji;
	private boolean search;
	public final String search_default;
	// Associated bots
	public final ArrayList<Bot> bots;
	// Mute
	private boolean mute;
	// Default function settings
	public final boolean func_greet;
	public final boolean func_html;
	public final boolean func_lastfm;
	public final boolean func_weather;
	public final boolean func_quote;
	public final boolean func_tell;
	public final boolean func_translate;
	public final boolean func_romaji;
	public final boolean func_search;

	public Chan(String name, boolean greet, String greet_visible, boolean html, boolean lastfm, boolean weather, boolean quote, boolean tell, boolean translate, boolean romaji,
			boolean search, String search_default, ArrayList<Bot> bots) {
		this.name = name;
		this.greet = this.func_greet = greet;
		this.greet_visible = (greet_visible.equals("public")) ? true : false;
		this.html = this.func_html = html;
		this.lastfm = this.func_lastfm = lastfm;
		this.weather = this.func_weather = weather;
		this.quote = this.func_quote = quote;
		this.tell = this.func_tell = tell;
		this.translate = this.func_translate = translate;
		this.romaji = this.func_romaji = romaji;
		this.search = this.func_search = search;
		this.search_default = search_default;
		this.mute = false;
		this.bots = bots;
	}

	public boolean getMute() {
		return mute;
	}

	public boolean getGreet() {
		return greet;
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

	public boolean getTranslate() {
		return translate;
	}

	public boolean getRomaji() {
		return romaji;
	}

	public boolean getSearch() {
		return search;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public void setGreet(boolean greet) {
		this.greet = greet;
	}

	public void setHtml(boolean html) {
		this.html = html;
	}

	public void setLastfm(boolean lastfm) {
		this.lastfm = lastfm;
	}

	public void setWeather(boolean weather) {
		this.weather = weather;
	}

	public void setQuote(boolean quote) {
		this.quote = quote;
	}

	public void setTell(boolean tell) {
		this.tell = tell;
	}

	public void setTranslate(boolean translate) {
		this.translate = translate;
	}

	public void setRomaji(boolean romaji) {
		this.romaji = romaji;
	}

	public void setSearch(boolean search) {
		this.search = search;
	}
}
