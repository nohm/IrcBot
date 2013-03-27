package org.snack.irc.handler;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Monitor;
import org.snack.irc.model.WeatherUser;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.WeatherAPI;

public class WeatherHandler implements Runnable {

	private final MessageEvent<?> event;

	public WeatherHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		getWeather();
	}

	/**
	 * Parses the event for the location, makes WeatherAPI return the weather
	 * and stores the location.
	 * 
	 * @param event
	 */
	private void getWeather() {
		DatabaseManager db = DatabaseManager.getInstance();
		WeatherUser user = db.getWeatherUser(event.getUser().getNick());
		String location = (event.getMessage().length() == 3) ? user.getLocation() : event.getMessage().split("we ")[1];

		String response;
		String data[] = WeatherAPI.getWeather(location);
		if (data == null) {
			response = Config.speech.get("WE_ERR");
		} else {
			response = Config.speech.get("WE_SUC").replace("<city>", data[0]).replace("<condition>", data[1]).replace("<degrees_c>", data[2]).replace("<degrees_f>", data[3])
					.replace("<wind_dir>", data[4]).replace("<wind_type>", data[5]).replace("<wind_speed>", data[6]).replace("<wind_speed_gust>", data[7])
					.replace("<humidity>", data[8]);
		}
		Monitor.print(response);
		event.getBot().sendMessage(event.getChannel(), response);

		if (!location.equals("")) {
			if (user.getName().equals("")) {
				Monitor.print("~INFO Put weatheruser: " + event.getUser().getNick() + " " + location);
				db.putWeatherUser(new WeatherUser(event.getUser().getNick(), location));
			} else if (!user.getLocation().equalsIgnoreCase(location)) {
				Monitor.print("~INFO Updated weatheruser: " + user.getName() + " " + location);
				db.updateWeatherUser(new WeatherUser(user.getName(), location));
			}
		}
	}
}
