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
	private boolean html;
	private boolean lastfm;
	private boolean weather;
	private boolean quote;
	private boolean tell;
	private boolean translate;
	private boolean romaji;
	// Associated bots
	public final ArrayList<Bot> bots;
	// Mute
	private boolean mute;
	// Default function settings
	public final boolean func_html;
	public final boolean func_lastfm;
	public final boolean func_weather;
	public final boolean func_quote;
	public final boolean func_tell;
	public final boolean func_translate;
	public final boolean func_romaji;

	public Chan(String name, boolean html, boolean lastfm, boolean weather, boolean quote, boolean tell, boolean translate, boolean romaji, ArrayList<Bot> bots) {
		this.name = name;
		this.html = this.func_html = html;
		this.lastfm = this.func_lastfm = lastfm;
		this.weather = this.func_weather = weather;
		this.quote = this.func_quote = quote;
		this.tell = this.func_tell = tell;
		this.translate = this.func_translate = translate;
		this.romaji = this.func_romaji = romaji;
		this.mute = false;
		this.bots = bots;
	}

	public boolean getMute() {
		return mute;
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

	public void setMute(boolean mute) {
		this.mute = mute;
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
}
