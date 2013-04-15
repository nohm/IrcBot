package org.snack.irc.handler.message;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.pircbotx.hooks.events.MessageEvent;
import org.snack.irc.main.TriggerHandler;
import org.snack.irc.model.Chan;
import org.snack.irc.model.HelpModule;
import org.snack.irc.settings.Config;

public class Help extends TriggerHandler {

	private MessageEvent<?> event;

	public Help() {}

	public Help(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		sendHelp();
	}

	/**
	 * Returns help based on the channels enabled functions
	 */
	private void sendHelp() {
		Chan chan = Config.channels.get(event.getChannel().getName());
		String hostmask = event.getUser().getHostmask();
		String permission = Config.admins.containsKey(hostmask) ? Config.admins.get(hostmask) : "all";
		String message = event.getMessage();
		ArrayList<String> response = new ArrayList<String>();

		if (message.substring(1).equals("help")) {
			response.add(Config.speech.get("modules"));
			for (Entry<String, HelpModule> module : Config.help.entrySet()) {
				if (module.getValue().hasPermission(permission)) {
					response.add(module.getValue().toString(chan));
				}
			}
		} else if (message.split(" ").length >= 2) {
			String unknown_module = Config.speech.get("unknown_module");
			String[] modules = message.split(" ");
			for (int i = 1; i < modules.length; i++) {
				boolean handled = false;
				for (Entry<String, HelpModule> module : Config.help.entrySet()) {
					if (module.getValue().hasPermission(permission)) {
						if (module.getValue().modules.get(modules[i]) != null) {
							response.add(module.getValue().modules.get(modules[i]));
							handled = true;
						}
					}
				}
				if (!handled) {
					unknown_module += modules[i] + ", ";
				}
			}
			if (!unknown_module.equals(Config.speech.get("unknown_module"))) {
				unknown_module = unknown_module.substring(0, unknown_module.length() - 2);
				response.add(unknown_module);
			}
		}

		for (String s : response) {
			event.getBot().sendNotice(event.getUser(), s);
		}
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (event.getMessage().length() >= 5 && event.getMessage().substring(1, 5).equals("help"));
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	@Override
	public boolean permission(Chan chan) {
		return true;
	}
}
