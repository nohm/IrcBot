package org.snack.irc.handler.message;

import java.net.URLEncoder;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.settings.Config;

public class BooruHandler extends TriggerHandler {

	private MessageEvent<?> event;

	public BooruHandler() {};

	public BooruHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		searchBooru();
	}

	private void searchBooru() {
		String response;
		try {
			String message = event.getMessage();
			String booru = message.substring(message.indexOf("-") + 1, message.indexOf(" "));

			String uriWord = URLEncoder.encode(message.substring(message.indexOf(" ") + 1).replaceAll("[^\\p{L}| ]", "").toLowerCase(), "utf-8");
			String url;
			if (booru.equals("safe") || booru.equals("safebooru")) {
				booru = "SafeBooru";
				url = "http://safebooru.org/index.php?page=post&s=list&tags=" + uriWord;
				response = Config.speech.get("BO_SUC").replace("<booru>", booru).replace("<url>", url);
			} else if (booru.equals("dan") || booru.equals("danbooru")) {
				booru = "SafeBooru";
				url = "http://donmai.us/posts?tags=" + uriWord;
				response = Config.speech.get("BO_SUC").replace("<booru>", booru).replace("<url>", url);
			} else if (booru.equals("kona") || booru.equals("konachan")) {
				booru = "KonaChan";
				url = "http://konachan.com/post?tags=" + uriWord;
				response = Config.speech.get("BO_SUC").replace("<booru>", booru).replace("<url>", url);
			} else if (booru.equals("gel") || booru.equals("gelbooru")) {
				booru = "GelBooru";
				url = "http://gelbooru.com/index.php?page=post&s=list&tags=" + uriWord;
				response = Config.speech.get("BO_SUC").replace("<booru>", booru).replace("<url>", url);
			} else if (booru.equals("loli") || booru.equals("lolibooru")) {
				booru = "GelBooru";
				url = "http://lolibooru.com/index.php?page=post&s=list&tags=" + uriWord;
				response = Config.speech.get("BO_SUC").replace("<booru>", booru).replace("<url>", url);
			} else {
				response = Config.speech.get("BO_ERR").replace("<booru>", booru);
			}
		} catch (Exception e) {
			response = "Invalid query.";
		}

		Monitor.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 6 && event.getMessage().substring(1, 6).equals("booru"));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}
