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
	private String name;
	// Functions that can be switched
	private boolean html;
	private boolean lastfm;
	private boolean weather;
	private boolean quote;
	private boolean tell;
	private boolean translate;
	private boolean romaji;
	// Associated bots
	private final ArrayList<Bot> bots;
	// Mute
	private boolean mute;
	// Default function settings
	private final boolean func_html;
	private final boolean func_lastfm;
	private final boolean func_weather;
	private final boolean func_quote;
	private final boolean func_tell;
	private final boolean func_translate;
	private final boolean func_romaji;

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

	public boolean getFunc_html() {
		return func_html;
	}

	public boolean getFunc_lastfm() {
		return func_lastfm;
	}

	public boolean getFunc_weather() {
		return func_weather;
	}

	public boolean getFunc_quote() {
		return func_quote;
	}

	public boolean getFunc_tell() {
		return func_tell;
	}

	public boolean getFunc_translate() {
		return func_translate;
	}

	public boolean getFunc_romaji() {
		return func_romaji;
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

	public boolean getTranslate() {
		return translate;
	}

	public boolean getRomaji() {
		return romaji;
	}

	public ArrayList<Bot> getBots() {
		return bots;
	}

	public void setMute(boolean mute) {
		this.mute = mute;
	}

	public void setName(String name) {
		this.name = name;
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
