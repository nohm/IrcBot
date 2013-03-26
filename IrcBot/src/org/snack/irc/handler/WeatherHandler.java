package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.model.WeatherUser;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.WeatherAPI;

public class WeatherHandler {

	/**
	 * Parses the event for the location, makes WeatherAPI return the weather
	 * and stores the location.
	 * 
	 * @param event
	 */
	public static void getWeather(MessageEvent<?> event) {
		DatabaseManager db = DatabaseManager.getInstance();
		String location = "";
		WeatherUser user = db.getWeatherUser(event.getUser().getNick());
		if (event.getMessage().length() == 3) {
			location = user.getLocation();
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

		if (location != "") {
			if (user.getName().equals("")) {
				db.putWeatherUser(new WeatherUser(event.getUser().getNick(), location));
			} else if (!user.getLocation().equalsIgnoreCase(location)) {
				db.updateWeatherUser(new WeatherUser(user.getName(), location));
			}
		}
	}
}
