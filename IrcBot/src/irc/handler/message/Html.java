package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.settings.Config;
import irc.worker.HtmlGetter;

import java.util.ArrayList;

import org.pircbotx.hooks.events.MessageEvent;

public class Html extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
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
				cleaned.add(toClean.substring(0, toClean.length() - 1));
			} else {
				cleaned.add(toClean);
			}
		}
		for (String toPrint : cleaned) {
			String response = HtmlGetter.getTitle(toPrint);
			Startup.print("~RESPONSE " + response);String youtube = toPrint.substring(toPrint.indexOf("://") + 3);
			if ((youtube.startsWith("youtube.") || youtube.startsWith("www.youtube.")) && youtube.contains("watch?v=") && youtube.split("/").length == 2 || (youtube.startsWith("youtu.be/") || youtube.startsWith("www.youtu.be/")) && youtube.split("/").length == 2) {
				event.getBot().sendMessage(event.getChannel(), response);
			} else {
				event.getBot().sendMessage(event.getChannel(), Config.speech.get("HT_TIT").replace("<title>", response));
			}
		}
	}
	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().contains("http://") || event.getMessage().contains("https://"));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("html")) {
			return chan.functions.get("html");
		} else {
			return true;
		}
	}
}
