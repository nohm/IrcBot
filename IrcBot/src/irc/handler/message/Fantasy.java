package irc.handler.message;

import irc.database.DatabaseManager;
import irc.enums.EventType;
import irc.main.FunctionTester;
import irc.main.Startup;
import irc.main.TriggerHandler;
import irc.model.Chan;
import irc.model.Permission;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

public class Fantasy extends TriggerHandler {

	private MessageEvent<?> event;

	@Override
	public void run() {
		handleCommand();
	}

	@Override
	public boolean trigger(MessageEvent<?> event) {
		String command;
		try {
			command = event.getMessage().split(" ")[0];
		} catch(Exception e) {
			command = event.getMessage();
		}
		boolean exists = false;
		for (String s : commands){
			if (command.equals(s)) {
				exists = true;
			}
		}
		return (event.getMessage().substring(0, 1).equals(".") && exists);
	}

	@Override
	public boolean permission(Chan chan) {
		return true;
	}

	@Override
	public void attachEvent(MessageEvent<?> event) {
		this.event = event;
	}

	private final String[] commands = {".init",".voice",".devoice",".hop",".dehop",".enable",".op",".deop",".sop",".desop",".owner",".deowner",".kick",".k",".kickban",".kb", ".sync"};

	private void handleCommand() {
		String message = event.getMessage();
		Channel channel = event.getChannel();
		User user = event.getUser();
		PircBotX bot = event.getBot();
		String response = "";

		if (!user.getChannelsHalfOpIn().contains(channel) && !user.getChannelsOpIn().contains(channel) && !user.getChannelsSuperOpIn().contains(channel) && !user.getChannelsOwnerIn().contains(channel)) {
			return;
		}

		//	.voice nick
		if (message.startsWith(".voice ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.voice(channel, u);
				}
			}
		}

		//	.devoice nick
		if (message.startsWith(".devoice ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.deVoice(channel, u);
				}
			}
		}

		if (!user.getChannelsOpIn().contains(channel) && !user.getChannelsSuperOpIn().contains(channel) && !user.getChannelsOwnerIn().contains(channel)) {
			return;
		}

		//	.kick nick reason // .k nick reason
		if (message.startsWith(".kick ") || message.startsWith(".k ") && message.split(" ").length >= 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.kick(channel, u);
				}
			}
		}

		//	.kickban nick reason // .kb nick reason
		if (message.startsWith(".kickban ") || message.startsWith(".kb") && message.split(" ").length >= 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.kick(channel, u);
					bot.sendRawLine("BAN " + channel.getName() + u.getNick() + "!" + u.getLogin() + "@" + user.getHostmask());
				}
			}
		}

		//	.hop nick
		if (message.startsWith(".hop ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.halfOp(channel, u);
				}
			}
		}

		//	.dehop nick
		if (message.startsWith(".dehop ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.deHalfOp(channel, u);
				}
			}
		}

		if (!user.getChannelsSuperOpIn().contains(channel) && !user.getChannelsOwnerIn().contains(channel)) {
			return;
		}

		//	.op nick
		if (message.startsWith(".op ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.op(channel, u);
				}
			}
		}

		//	.deop nick
		if (message.startsWith(".deop ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.deOp(channel, u);
				}
			}
		}

		if (!user.getChannelsOwnerIn().contains(channel)) {
			return;
		}

		//	.sop nick
		if (message.startsWith(".sop ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.superOp(channel, u);
				}
			}
		}

		//	.desop nick
		if (message.startsWith(".desop ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.deSuperOp(channel, u);
				}
			}
		}

		//	.owner
		if (message.startsWith(".owner ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.owner(channel, u);
				}
			}
		}

		//	.deowner
		if (message.startsWith(".deowner ") && message.split(" ").length == 2) {
			for (User u : channel.getUsers()) {
				if (u.getNick().equalsIgnoreCase(message.split(" ")[1]) && higherRank(channel, user, u) && !higherRank(channel, u, user)) {
					bot.deOwner(channel, u);
				}
			}
		}

		for (User u : channel.getUsers()) {
			DatabaseManager.getInstance().putPermission(new Permission(u.getNick(), channel.getName(), ""));
			// TODO: get rank from channel
			// TODO: check for rank of user a + b, no overclassing.
			// TODO: check for ranks onJoin
			// TODO: test ban, add reasons to kick
		}

		// .sync
		if (message.equals(".sync")) {
			for (User u : channel.getUsers()){
				Permission p = DatabaseManager.getInstance().getPermission(user.getNick(), channel.getName());
				if (!p.getName().equals("")) {
					if (p.getPermission().equals("VOICE")) {
						bot.voice(channel, u);
					} else if (p.getPermission().equals("HALFOP")) {
						bot.halfOp(channel, u);
					} else if (p.getPermission().equals("OP")) {
						bot.op(channel, u);
					} else if (p.getPermission().equals("SUPEROP")) {
						bot.superOp(channel, u);
					} else if (p.getPermission().equals("OWNER")) {
						bot.owner(channel, u);
					}
				}
			}
		}


		// .init has no handle, just triggers this
		for (User u : channel.getUsers()) {
			String rank = "NORMAL";
			if (user.getChannelsVoiceIn().contains(channel)) {
				rank = "VOICE";
			} else if (user.getChannelsHalfOpIn().contains(channel)) {
				rank = "HALFOP";
			} else if (user.getChannelsOpIn().contains(channel)) {
				rank = "OP";
			} else if (user.getChannelsSuperOpIn().contains(channel)) {
				rank = "SUPEROP";
			} else if (user.getChannelsOwnerIn().contains(channel)) {
				rank = "OWNER";
			}
			DatabaseManager.getInstance().putPermission(new Permission(u.getNick(), channel.getName(), rank));
		}

		if (!response.equals("")) {
			new FunctionTester(event, channel, event.getUser(), EventType.COMMAND).run();
			Startup.print("~INFO Response: " + response);
			event.getBot().sendNotice(event.getUser(), response);
		}
	}

	private static boolean higherRank(Channel c, User a, User b) {
		if (a.getChannelsVoiceIn().contains(c) && !b.getChannelsVoiceIn().contains(c)) {
			return true;
		} else if (a.getChannelsHalfOpIn().contains(c) && !b.getChannelsHalfOpIn().contains(c)) {
			return true;
		} else if (a.getChannelsOpIn().contains(c) && !b.getChannelsOpIn().contains(c)) {
			return true;
		} else if (a.getChannelsSuperOpIn().contains(c) && !b.getChannelsSuperOpIn().contains(c)) {
			return true;
		} else if (a.getChannelsOwnerIn().contains(c) && !b.getChannelsOwnerIn().contains(c)) {
			return true;
		}
		return false;
	}
}
