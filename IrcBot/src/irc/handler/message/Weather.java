package irc.handler.message;

import irc.database.DatabaseManager;
import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.model.WeatherUser;
import irc.settings.Config;
import irc.worker.WeatherAPI;

import org.pircbotx.hooks.events.MessageEvent;

public class Weather extends TriggerHandler implements Runnable {

	private MessageEvent<?> event;

	@Override
	public void run() {
		DatabaseManager db = DatabaseManager.getInstance();
		WeatherUser user = db.getWeatherUser(event.getUser().getNick());
		String location = (event.getMessage().length() == 3) ? user.getLocation() : event.getMessage().split("we ")[1];
		location = (event.getMessage().split(" ").length == 2 && event.getMessage().split(" ")[1].startsWith("@")) ? db.getWeatherUser(event.getMessage().split(" ")[1].substring(1)).getLocation() : location;

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

		if (!location.equals("") && event.getMessage().split(" ").length == 2 && !event.getMessage().split(" ")[1].startsWith("@")) {
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
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && (event.getMessage().length() == 3 && event.getMessage().substring(1, 3).equals("we")) || event.getMessage().length() >= 5 && event.getMessage().substring(1, 4).equals("we "));
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
