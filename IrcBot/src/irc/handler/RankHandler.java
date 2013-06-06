package irc.handler;

import irc.database.DatabaseManager;
import irc.model.Permission;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.JoinEvent;

public class RankHandler implements Runnable {

	private final JoinEvent<?> event;

	public RankHandler(JoinEvent<?> event) {
		this.event = event;
	}

	@Override
	public void run() {
		Channel channel = event.getChannel();
		User user = event.getUser();
		PircBotX bot = event.getBot();

		Permission p = DatabaseManager.getInstance().getPermission(user.getNick(), channel.getName());
		if (!p.getName().equals("")) {
			if (p.getPermission().equals("VOICE")) {
				bot.voice(channel, user);
			} else if (p.getPermission().equals("HALFOP")) {
				bot.halfOp(channel, user);
			} else if (p.getPermission().equals("OP")) {
				bot.op(channel, user);
			} else if (p.getPermission().equals("SUPEROP")) {
				bot.superOp(channel, user);
			} else if (p.getPermission().equals("OWNER")) {
				bot.owner(channel, user);
			}
		}
	}
}
