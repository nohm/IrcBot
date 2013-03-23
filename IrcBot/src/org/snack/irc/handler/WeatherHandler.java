package org.snack.irc.handler;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.model.WeatherUser;
import org.snack.irc.settings.Config;
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
		if (data == null) {
			event.getBot().sendMessage(event.getChannel(), Config.speech.get("WE_ERR"));
		} else {
			event.getBot().sendMessage(
					event.getChannel(),
					Config.speech.get("WE_SUC").replace("<city>", data[0]).replace("<condition>", data[1]).replace("<degrees_c>", data[2]).replace("<degrees_f>", data[3])
							.replace("<wind_dir>", data[4]).replace("<wind_type>", data[5]).replace("<wind_speed>", data[6]).replace("<wind_speed_gust>", data[7])
							.replace("<humidity>", data[8]));
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
