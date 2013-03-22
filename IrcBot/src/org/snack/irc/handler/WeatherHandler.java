package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.WeatherUser;
import org.snack.irc.settings.SettingParser;
import org.snack.irc.settings.SettingStorer;
import org.snack.irc.worker.WeatherAPI;

public class WeatherHandler {

	/**
	 * Parses the event for the location, makes WeatherAPI return the weather
	 * and stores the location.
	 * 
	 * @param event
	 */
	public static void getWeather(MessageEvent<?> event) {
		ArrayList<WeatherUser> storage;
		try {
			storage = SettingParser.parseWUsers();
		} catch (Exception e) {
			// e.printStackTrace();
			storage = new ArrayList<WeatherUser>();
		}

		int changeNum = -1;
		String location = "";

		if (event.getMessage().equalsIgnoreCase(",we") || event.getMessage().equalsIgnoreCase(".we") || event.getMessage().equalsIgnoreCase("!we")) {
			for (WeatherUser user : storage) {
				if (user.getName().equals(event.getUser().getNick())) {
					location = user.getLocation();
				}
			}
		} else {
			location = event.getMessage().split("we ")[1];
		}

		String data[] = WeatherAPI.getWeather(location);
		if (data[0] == null || data[1] == null || data[2] == null || data[3] == null || data[4] == null) {
			event.getBot().sendMessage(event.getChannel(), data[0]);
		} else {
			event.getBot().sendMessage(event.getChannel(), data[0] + ": " + data[1] + " " + data[2] + "°C/" + data[3] + "°F Wind: " + data[4] + " Humidity: " + data[5]);
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
				e.printStackTrace();
			}
		}
	}
}
