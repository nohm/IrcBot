package irc.handler.message;

import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.settings.Config;

import org.pircbotx.hooks.events.MessageEvent;

public class SysInfo extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		String response = "Bot info - JRE Vendor: " + System.getProperty("java.vendor") + " - JRE Version: " + System.getProperty("java.version") + " - OS Name: " + System.getProperty("os.name") + " - OS Version: " + System.getProperty("os.version") + " - OS Arch: " + System.getProperty("os.arch") + " - OS Language: " + System.getProperty("user.language");
		Startup.print("~INFO Response: " + response);
		event.getBot().sendMessage(event.getChannel(), response);
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		return (Config.sett_str.get("IDENTIFIERS").contains(event.getMessage().substring(0, 1)) && event.getMessage().length() == 8 && event.getMessage().endsWith("sysinfo"));
	}

	@Override
	public boolean permission(Chan chan) {
		if (chan.functions.containsKey("sysinfo")) {
			return chan.functions.get("sysinfo");
		}
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}
}