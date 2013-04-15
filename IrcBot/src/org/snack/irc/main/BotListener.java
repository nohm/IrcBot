package org.snack.irc.main;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.DisconnectEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UserListEvent;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.snack.irc.database.DatabaseManager;
import org.snack.irc.enums.EventType;
import org.snack.irc.handler.GreetHandler;
import org.snack.irc.handler.TellHandler;
import org.snack.irc.model.Bot;
import org.snack.irc.model.Chan;
import org.snack.irc.model.LastMsg;
import org.snack.irc.model.Quote;
import org.snack.irc.settings.Config;

/**
 * The handler for text events recieved by the bot.
 * 
 * @author snack
 * 
 */
@SuppressWarnings("rawtypes")
public class BotListener extends ListenerAdapter implements Listener {

	private static PircBotX bot;
	private final ExecutorService executor;
	DatabaseManager db;
	Set<Class<? extends TriggerHandler>> handlers;

	public BotListener(PircBotX bot) {
		BotListener.bot = bot;
		executor = Executors.newFixedThreadPool(5);
		db = DatabaseManager.getInstance();

		List<ClassLoader> classLoadersList = new LinkedList<ClassLoader>();
		classLoadersList.add(ClasspathHelper.contextClassLoader());
		classLoadersList.add(ClasspathHelper.staticClassLoader());

		Reflections reflections = new Reflections(new ConfigurationBuilder()
		.setScanners(new SubTypesScanner(false /* don't exclude Object.class */), new ResourcesScanner())
		.setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
		.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("org.snack.irc.handler.message"))));

		handlers = reflections.getSubTypesOf(TriggerHandler.class);
	}

	/**
	 * Called on every message, determines what to do with it.
	 */
	@Override
	public void onMessage(MessageEvent event) {
		Channel channel = event.getChannel();
		Chan chan = Config.channels.get(channel.getName());
		String message = event.getMessage();
		User user = event.getUser();
		String nick = user.getNick();
		Monitor.print("<<MSG " + chan.name + " " + nick + ": " + message);

		// Store last occurence of username
		if ((System.currentTimeMillis() - db.getLastMsg(nick).getTime()) > (10 * 60 * 1000)) { // 10m
			executor.execute(new TellHandler(event, null, null));
		}
		db.putLastMsg(new LastMsg(nick, System.currentTimeMillis()));

		// Command handler
		if (!chan.mute) {
			boolean handled = false;
			for (Class<? extends TriggerHandler> c : handlers) {
				try {
					Class<?> loadedClass = Class.forName(c.getName());
					Class cl = null;
					if(TriggerHandler.class.isAssignableFrom(loadedClass)) {
						cl = loadedClass.asSubclass(TriggerHandler.class);
					}
					TriggerHandler tHandler = (TriggerHandler) cl.newInstance();
					if (tHandler.trigger(event) && tHandler.permission(chan) && !handled) {
						tHandler.attachEvent(event);
						executor.execute(tHandler);
						handled = true;
					}
					// System.out.println(tHandler.getClass().getName() + ": " + tHandler.trigger(event) + " ; " + tHandler.permission(chan));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			boolean add = true;
			for (Bot b : chan.bots) {
				if (b.name.equalsIgnoreCase(nick)) {
					add = false;
				}
			}
			if (Config.sett_str.get("BOT_NAME").equalsIgnoreCase(nick)) {
				add = false;
			}
			// Add random quotes
			if (new Random().nextInt(100) > 90 && add && message.length() <= Config.sett_int.get("QUOTE_MAX") && message.length() >= Config.sett_int.get("QUOTE_MIN")) {
				Monitor.print("~INFO Added quote: " + chan.name + " <" + user.getNick() + "> " + message);
				DatabaseManager.getInstance().putQuote(new Quote(chan.name, user.getNick(), message));
			}
		}
	}

	/**
	 * Auto-rejoin on kick.
	 */
	@Override
	public void onKick(KickEvent event) {
		Monitor.print("<<KICK " + event.getChannel().getName());
		event.getBot().sendRawLine("JOIN " + event.getChannel().getName());
		Monitor.print("~INFO Rejoined");
	}

	/**
	 * Called whenever someone (or the bot) joins, updates the functions.
	 */
	@Override
	public void onJoin(JoinEvent event) {
		if (!event.getUser().getNick().equals(Config.sett_str.get("BOT_NAME"))) {
			Monitor.print("<<JOIN " + event.getChannel().getName() + " " + event.getUser().getNick());
			executor.execute(new FunctionTester(event, event.getChannel(), event.getUser(), EventType.JOIN));
			Chan chan = Config.channels.get(event.getChannel().getName());
			if (chan.functions.get("greet")) {
				executor.execute(new GreetHandler(event));
			}
		}
		executor.execute(new TellHandler(null, event, null));
		Monitor.print("~INFO Cleaned tells: " + event.getChannel().getName());
	}

	/**
	 * Called whenever someone (or the bot) parts/leaves, updates the functions.
	 */
	@Override
	public void onPart(PartEvent event) {
		Monitor.print("<<PART " + event.getChannel().getName() + " " + event.getUser().getNick());
		executor.execute(new FunctionTester(event, event.getChannel(), event.getUser(), EventType.PART));
	}

	/**
	 * Called whenever someone (or the bot) quits, updates the functions. Checks
	 * all channels because the user can leave unlimited channels at once
	 */
	@Override
	public void onQuit(QuitEvent event) {
		Monitor.print("<<QUIT " + event.getUser().getNick());
		if (event.getUser().getNick().equals(Config.sett_str.get("BOT_NAME"))) {
			bot.setName(Config.sett_str.get("BOT_NAME"));
		}
		executor.execute(new FunctionTester(event, null, event.getUser(), EventType.QUIT));
	}

	/**
	 * Called when the bot has fully started, updates the functions.
	 */
	@Override
	public void onUserList(UserListEvent event) {
		Monitor.print("<<USERLIST");
		executor.execute(new FunctionTester(event, event.getChannel(), null, EventType.USERLIST));
	}

	@Override
	public void onDisconnect(DisconnectEvent event) {
		Monitor.print("<<<Disconnected");
		while (!event.getBot().isConnected()) {
			try {
				Thread.sleep(15000);
				if (bot.getName().equals(Config.sett_str.get("BOT_NAME"))) {
					bot.setName(Config.sett_str.get("BOT_ALT_NAME"));
				} else {
					bot.setName(Config.sett_str.get("BOT_NAME"));
				}
				event.getBot().reconnect();
				for (Chan channel : Config.channels.values()) {
					if (channel.join) {
						event.getBot().sendRawLine("JOIN " + channel.name);
					}
				}
			} catch (Exception e) {
				onDisconnect(event);
			}
		}
	}

	@Override
	public void onAction(ActionEvent event) {
		Monitor.print("<<ACTION " + event.getChannel().getName() + " " + event.getUser().getNick() + ": " + event.getMessage());
	}

	@Override
	public void onNickChange(NickChangeEvent event) {
		Monitor.print("<<NICKCHANGE");
		executor.execute(new TellHandler(null, null, event));
	}

	public static void sendCustomMessage(String type, String target, String message) {
		if (type.equalsIgnoreCase("PRIVMSG") && (!target.equals("") || target == null)) {
			if (target.equals("#")) {
				Monitor.print(">>BROADCAST " + message);
				for (Channel channel : bot.getChannels()) {
					bot.sendMessage(channel, "Broadcast: " + message);
				}
			} else {
				Monitor.print(">>MSG " + target + ": " + message);
				bot.sendMessage(target, message);
			}
		} else if (type.equalsIgnoreCase("ACTION")) {
			Monitor.print(">>ACTION " + target + ": " + message);
			bot.sendAction(target, message);
		} else {
			Monitor.print(">>RAW " + type + " " + target + ": " + message);
			bot.sendRawLine(type + " " + target + " :" + message);
		}
	}
}