package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.main.Startup;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.model.WeatherUser;
import org.snack.irc.settings.Config;
import org.snack.irc.worker.WeatherAPI;

public class Weather extends TriggerHandler implements Runnable {

	private MessageEvent<?> event;

	@Override
	public void run() {
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
		Startup.print(response);
		event.getBot().sendMessage(event.getChannel(), response);

		if (!location.equals("")) {
			if (user.getName().equals("")) {
				Startup.print("~RESPONSE  Put weatheruser: " + event.getUser().getNick() + " " + location);
				db.putWeatherUser(new WeatherUser(event.getUser().getNick(), location));
			} else if (!user.getLocation().equalsIgnoreCase(location)) {
				Startup.print("~RESPONSE  Updated weatheruser: " + user.getName() + " " + location);
				db.updateWeatherUser(new WeatherUser(user.getName(), location));
			}
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() >= 3 && event.getMessage().substring(1, 3).equals("we"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("weather")) {
			return chan.functions.get("weather");
		} else {
			return true;
		}
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
