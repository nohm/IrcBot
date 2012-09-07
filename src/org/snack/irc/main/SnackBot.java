package org.snack.irc.main;
import java.util.ArrayList;

import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.html.HtmlGetter;
import org.snack.irc.lastfm.LastfmAPI;
import org.snack.irc.lastfm.LastfmUser;
import org.snack.irc.settings.SettingParser;
import org.snack.irc.settings.SettingStorer;
import org.snack.irc.weather.WeatherAPI;
import org.snack.irc.weather.WeatherUser;


/**
 * The handler for text events recieved by the bot.
 * @author snack
 * 
 */
@SuppressWarnings("rawtypes")
public class SnackBot extends ListenerAdapter implements Listener {
	
	/**
	 * Last.fm API keys.
	 * Your API Key is 97f9215a0928d47eca2b08408f252ba7
	 * Your secret is 74071ee6cf4270a5a7b0ad8b7150eb4c2
	 */
    
	/**
	 * Called on every message, determines what to do with it.
	 */
	@Override
    public void onMessage(MessageEvent event) throws Exception {
    	
		//Call for weather
        if (event.getMessage().startsWith(",we ") || event.getMessage().equals(",we")
        	|| event.getMessage().startsWith(".we ") || event.getMessage().equals(".we")) {
        	getWeather(event);
			
        //Call for now playing
        } /*else if (event.getMessage().startsWith(",np ") || event.getMessage().equals(",np")
        	|| event.getMessage().startsWith(".np ") || event.getMessage().equals(".np")) {
        	getLastfm(event);
        
        //Call for HTML Title
        } else if (event.getMessage().contains("http://") || event.getMessage().contains("https://")) {
        	getHTMLTitle(event);
        	
        } else if (event.getMessage().contains(",dis")) {
        	event.getBot().disconnect();
        }*/
    }
	
	@Override
	public void onDisconnect(DisconnectEvent event) throws Exception {
		try {
			event.getBot().connect("irc.rizon.net");
			event.getBot().sendRawLine("NICKSERV IDENTIFY 1.pieps");
			event.getBot().sendRawLine("JOIN #pantsumen");
		} catch(Exception e) {
			Thread.sleep(5000);
			onDisconnect(event);
		}
	}
	
	/**
	 * Parses an event's text for http(s) links, cleans them and makes HtmlGetter return their titles.
	 * @param event
	 */
	private void getHTMLTitle(MessageEvent event) {
		String[] split = event.getMessage().split(" ");
		ArrayList<String> links = new ArrayList<String>();
		for (String toLinks : split) {
			if (toLinks.startsWith("http://") || toLinks.startsWith("https://")) {
				links.add(toLinks);
			}
		}
		ArrayList<String> cleaned = new ArrayList<String>(links.size());
		for (String toClean : links) {
			if (toClean.endsWith(",") || toClean.endsWith(".") || toClean.endsWith(":") || toClean.endsWith(";")) {
				cleaned.add(toClean.substring(0, toClean.length() -1));
			} else {
				cleaned.add(toClean);
			}
		}
		for (String toPrint : cleaned) {
			event.getBot().sendMessage(event.getChannel(), HtmlGetter.getTitle(toPrint));
		}
	}
    
	/**
	 * Parses the event for the username, makes LastfmAPI return their now playing/ last played and stores the username.
	 * @param event
	 */
    private void getLastfm(MessageEvent event) {
    	ArrayList<LastfmUser> storage;
		try {
			storage = SettingParser.parseLUsers();
		} catch (Exception e) {
			//e.printStackTrace();
			storage = new ArrayList<LastfmUser>();
		}
    	
		int changeNum = -1;
		String username = "";
		
		if (event.getMessage().equalsIgnoreCase(",np") || event.getMessage().equalsIgnoreCase(".np")) {
			for (LastfmUser user : storage) {
				if (user.getName().equals(event.getUser().getNick())) {
					username = user.getUsername();
				}
			}
		} else {
			username = event.getMessage().split("np ")[1];
		}
		
		String data[] = LastfmAPI.getSong(username);
		if (data[0]  == null || data[1] == null || data[2] == null || data[3] == null) {
			event.getBot().sendMessage(event.getChannel(), data[0]);
		} else {
			if (data[0].equals("true")) {
				event.getBot().sendMessage(event.getChannel(), username + " is now playing: " + data[2] + " by " + data[1] + " on " + data[3]);
			} else {
				event.getBot().sendMessage(event.getChannel(), username + " last played: " + data[2] + " by " + data[1] + " on " + data[3]);
			}
		}
		
		for (int i = 0; i < (storage.size()); i++) {
			if (storage.get(i).getName().equals(event.getUser().getNick())) {
				changeNum = i;
			}
		}
		if (username != "") {
			if (changeNum == -1) {
	    		storage.add(new LastfmUser(event.getUser().getNick(), username));
			} else {
				storage.set(changeNum, new LastfmUser(event.getUser().getNick(), username));
			}
			
			try {
				SettingStorer.storeLUsers(storage);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
    
	/**
	 * Parses the event for the location, makes WeatherAPI return the weather and stores the location.
	 * @param event
	 */
    private void getWeather(MessageEvent event) {
    	ArrayList<WeatherUser> storage;
		try {
			storage = SettingParser.parseWUsers();
		} catch (Exception e) {
			//e.printStackTrace();
			storage = new ArrayList<WeatherUser>();
		}
    	
		int changeNum = -1;
		String location = "";
		
		if (event.getMessage().equalsIgnoreCase(",we") || event.getMessage().equalsIgnoreCase(".we")) {
			for (WeatherUser user : storage) {
				if (user.getName().equals(event.getUser().getNick())) {
					location = user.getLocation();
				}
			}
		} else {
        	location = event.getMessage().split("we ")[1];
		}
		
		String data[] = WeatherAPI.getWeather(location);
		if (data[0]  == null || data[1] == null || data[2] == null || data[3] == null || data[4] == null) {
			event.getBot().sendMessage(event.getChannel(), data[0]);
		} else {
			event.getBot().sendMessage(event.getChannel(), data[0] + ": " + data[1] + " " + data[2] + "°C/" + data[3] + "°F Wind: " + data[4]  + " Humidity: " + data[5]);
		}
		for (int i = 0; i < (storage.size()); i++) {
			if (storage.get(i).getName().equals(event.getUser().getNick())) {
				changeNum = i;
			}
		}
		if (location != "") {
			if (changeNum == -1) {
	    		storage.add(new WeatherUser(event.getUser().getNick(), location));
			} else {
				storage.set(changeNum, new WeatherUser(event.getUser().getNick(), location));
			}
			
			try {
				SettingStorer.storeWUsers(storage);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
    }
}