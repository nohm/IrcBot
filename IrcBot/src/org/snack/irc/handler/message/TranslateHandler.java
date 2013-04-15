package org.snack.irc.handler.message;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.Monitor;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.settings.Config;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

public class TranslateHandler extends TriggerHandler {

	private MessageEvent<?> event;

	public TranslateHandler() {}

	public TranslateHandler(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		translate();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 11 && event.getMessage().substring(1, 11).equals("translate "));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	/**
	 * Translates text using the Bing translate API
	 */
	private void translate() {
		String text = event.getMessage().substring(11); // Cut off the command

		// Set your Windows Azure Marketplace client info - See
		// http://msdn.microsoft.com/en-us/library/hh454950.aspx
		Translate.setClientId("92ef8ea3-08d0-4642-8f7c-af1898ac47b6");
		Translate.setClientSecret("i4nycktHpUOs5eTgvo1AabpVSUGPqbgrVydJF2nVmtM=");

		String translatedText = "";
		try {
			String response = Translate.execute(text, Language.AUTO_DETECT, Language.ENGLISH);
			if (response.startsWith("TranslateApiException:")) {
				throw new Exception();
			}
			translatedText = Config.speech.get("TR_SUC").replace("<response>", Translate.execute(text, Language.AUTO_DETECT, Language.ENGLISH));
		} catch (Exception e) {
			translatedText = Config.speech.get("TR_ERR");
		}

		Monitor.print("~RESPONSE  Translate: " + translatedText);
		event.getBot().sendMessage(event.getChannel(), translatedText);
	}
}
