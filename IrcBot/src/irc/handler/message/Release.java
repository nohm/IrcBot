package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.main.Utils;
import irc.model.Chan;
import irc.settings.Config;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pircbotx.hooks.events.MessageEvent;

public class Release extends TriggerHandler {

	private MessageEvent<?> event;
	private final String[] dayNames = "monday tuesday wednesday thursday friday saturday sunday".split(" ");

	@Override
	public void run() {
		ArrayList<String> results = new ArrayList<String>();
		try {
			String input = event.getMessage();
			String command = input.split(" ")[1];

			GregorianCalendar calJp = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"));
			int curDayMonth = calJp.get(Calendar.DAY_OF_MONTH) - 1;
			Document doc = Jsoup.parse(Utils.httpRequest("http://www.animecalendar.net/").toString());
			Elements days = doc.select("div[class^=da]");
			int nextMonth = calJp.get(Calendar.MONTH) == 12 ? 0 : calJp.get(Calendar.MONTH);
			int year = nextMonth == 0 ? calJp.get(Calendar.YEAR) + 1 : calJp.get(Calendar.YEAR);
			doc = Jsoup.parse(Utils.httpRequest("http://www.animecalendar.net/" + year + "/" + nextMonth).toString());
			days.addAll(doc.select("div[class^=da]"));

			boolean contains = false;
			for (String s : dayNames) {
				if (command.equalsIgnoreCase(s)) {
					contains = true;
				}
			}

			if (contains || command.equalsIgnoreCase("today") || command.equalsIgnoreCase("tomorrow")) {
				if (contains) {
					curDayMonth = parseDayName(command);
				} else if (command.equalsIgnoreCase("tomorrow")) {
					curDayMonth += 1;
				}
				results.addAll(getViews(days, curDayMonth));
			} else {
				for (int i = 0; i < 7; i++) {
					curDayMonth = parseDayName(dayNames[i]);
					results.addAll(getViews(days, curDayMonth));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			results.add("Unable to retrieve :(");
		}

		for (String s : results) {
			Startup.print("~INFO Response: " + s);
			event.getBot().sendNotice(event.getUser(), s);
		}
	}

	private ArrayList<String> getViews(Elements days, int day) {
		ArrayList<String> results = new ArrayList<String>();
		String showDate = days.get(day).select("thead").select("h2").select("a").attr("href").replaceFirst("/", "");
		Elements shows = days.get(day).select("table").select("tbody").select("div.tooltip");
		for (Element show : shows) {
			String showName = show.select("td.tooltip_title").select("h4").text();
			String showTime = show.select("td.tooltip_info").select("h4").text().split(" on")[0];
			String airTime = showTime.split("at ")[1].split(" ")[0] + ":00";
			String airDate = showDate.replace("/", "-");
			String[] airTimeArray = airTime.split(":");
			String[] airDateArray = airDate.split("-");
			String timeUntil = getTimeUntil(Integer.valueOf(airDateArray[0]), Integer.valueOf(airDateArray[1]), Integer.valueOf(airDateArray[2]), Integer.valueOf(airTimeArray[0]), Integer.valueOf(airTimeArray[1]), Integer.valueOf(airTimeArray[2]));
			try {
				results.add(URLDecoder.decode(showName, "UTF-8") + " - " + showTime + " [" + timeUntil + "]");
			} catch (Exception e) {
				results.add(showName + " - " + showTime + " [" + timeUntil + "]");
			}
		}
		return results;
	}

	private int parseDayName(String day) {
		GregorianCalendar airJp = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"));
		airJp.setTimeInMillis(new GregorianCalendar().getTimeInMillis());
		int curDay = airJp.get(Calendar.DAY_OF_WEEK) - 1;
		int destDay = 0;
		for (int i = 0; i < 7; i++) {
			if (dayNames[i].equalsIgnoreCase(day)) {
				destDay = i;
			}
		}
		if (destDay < curDay) {
			destDay += 7;
		}
		return airJp.get(Calendar.DAY_OF_MONTH) + (destDay - curDay);
	}

	private String getInHMS(long millis) {
		long diffInDays  = millis/1000/86400;
		long diffInHours = (millis/1000 - 86400*diffInDays) / 3600;
		long diffInMins  = (millis/1000 - 86400*diffInDays - 3600*diffInHours) / 60;
		long diffInSecs  = (millis/1000 - 86400*diffInDays - 3600*diffInHours - 60*diffInMins);
		String dayStr = diffInDays < 10 ? "0" + diffInDays : "" + diffInDays;
		String hourStr = diffInHours < 10 ? "0" + diffInHours : "" + diffInHours;
		String minStr = diffInMins < 10 ? "0" + diffInMins : "" + diffInMins;
		String secStr = diffInSecs < 10 ? "0" + diffInSecs : "" + diffInSecs;
		if (diffInDays == 0) {
			if (diffInHours == 0) {
				return minStr + ":" + secStr;
			}
			return hourStr + ":" + minStr + ":" + secStr;
		}
		return dayStr + ":" + hourStr + ":" + minStr + ":" + secStr;
	}

	private String getTimeUntil(int year, int month, int day, int hours, int minutes, int seconds) {
		GregorianCalendar nowJp = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"));
		GregorianCalendar airJp = new GregorianCalendar(TimeZone.getTimeZone("Asia/Tokyo"));
		airJp.set(year, month - 1, day + 1, hours, minutes, seconds);
		// 86400000 yay timezones, TODO: probably not portable at all!
		long aired = (((airJp.getTimeInMillis() - 86400000) - nowJp.getTimeInMillis()));
		if (aired < 0) {
			return "Aired " + getInHMS(aired * -1) + " ago";
		}
		return getInHMS(aired);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().split(" ")[0].endsWith("release") && event.getMessage().split(" ").length == 2);
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("release")) {
			return chan.functions.get("release");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
