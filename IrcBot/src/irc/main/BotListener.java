package irc.main;

import irc.database.DatabaseManager;
import irc.enums.EventType;
import irc.handler.GreetHandler;
import irc.handler.RankHandler;
import irc.handler.RulesHandler;
import irc.handler.TellHandler;
import irc.model.Bot;
import irc.model.Chan;
import irc.model.LastMsg;
import irc.model.Quote;
import irc.settings.Config;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.pircbotx.Channel;
import org.pircbotx.Colors;
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
		.filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("irc.handler.message")))); // VERY CAREFUL WITH REFACTORING

		handlers = reflections.getSubTypesOf(TriggerHandler.class);
	}

	/**
	 * Called on every message, determines what to do with it.
	 */
	@Override
	public void onMessage(MessageEvent event) {
		Channel channel = event.getChannel();
		Chan chan = Config.channels.get(channel.getName());
		String message = Colors.removeColors(event.getMessage());
		User user = event.getUser();
		String nick = user.getNick();
		Startup.print("<<MSG " + chan.name + " " + nick + ": " + message);

		// Store last occurence of username
		if ((System.currentTimeMillis() - db.getLastMsg(nick).getTime()) > (10 * 60 * 1000)) { // 10m
			executor.execute(new TellHandler(event, null, null));
		}
		db.putLastMsg(new LastMsg(nick, System.currentTimeMillis(), event.getMessage()));

		// Command handler
		if (!chan.mute && (!(chan.ignore.containsKey(nick) && chan.ignore.get(nick)) || !Config.admins.containsKey(user.getHostmask()))) {
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

			if (!handled) {
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
					Startup.print("~INFO Added quote: " + chan.name + " <" + user.getNick() + "> " + message);
					DatabaseManager.getInstance().putQuote(new Quote(chan.name, user.getNick(), message));
				}
			}
		}
	}

	/**
	 * Auto-rejoin on kick.
	 */
	@Override
	public void onKick(KickEvent event) {
		Startup.print("<<KICK " + event.getChannel().getName());
		event.getBot().sendRawLine("JOIN " + event.getChannel().getName());
		Startup.print("~INFO Rejoined");
	}

	/**
	 * Called whenever someone (or the bot) joins, updates the functions.
	 */
	@Override
	public void onJoin(JoinEvent event) {
		if (!event.getUser().getNick().equals(Config.sett_str.get("BOT_NAME"))) {
			Startup.print("<<JOIN " + event.getChannel().getName() + " " + event.getUser().getNick());
			executor.execute(new FunctionTester(event, event.getChannel(), event.getUser(), EventType.JOIN));
			Chan chan = Config.channels.get(event.getChannel().getName());
			if (chan.functions.get("greet")) {
				executor.execute(new GreetHandler(event));
			}
			if (chan.functions.get("rules") && !db.containsUser(new LastMsg(event.getUser().getNick(), 0, ""))) {
				executor.execute(new RulesHandler(event));
			}
			//if (chan.functions.get("fantasy")) {
			executor.execute(new RankHandler(event));
			//}
		}
		executor.execute(new TellHandler(null, event, null));
		Startup.print("~INFO Cleaned tells: " + event.getChannel().getName());
		db.putLastMsg(new LastMsg(event.getUser().getNick(), System.currentTimeMillis(), ""));
	}

	/**
	 * Called whenever someone (or the bot) parts/leaves, updates the functions.
	 */
	@Override
	public void onPart(PartEvent event) {
		Startup.print("<<PART " + event.getChannel().getName() + " " + event.getUser().getNick());
		executor.execute(new FunctionTester(event, event.getChannel(), event.getUser(), EventType.PART));
	}

	/**
	 * Called whenever someone (or the bot) quits, updates the functions. Checks
	 * all channels because the user can leave unlimited channels at once
	 */
	@Override
	public void onQuit(QuitEvent event) {
		Startup.print("<<QUIT " + event.getUser().getNick());
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
		Startup.print("<<USERLIST");
		executor.execute(new FunctionTester(event, event.getChannel(), null, EventType.USERLIST));
	}

	@Override
	public void onDisconnect(DisconnectEvent event) {
		Startup.print("<<<Disconnected");
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
		Startup.print("<<ACTION " + event.getChannel().getName() + " " + event.getUser().getNick() + ": " + event.getMessage());
		db.putLastMsg(new LastMsg(event.getUser().getNick(), System.currentTimeMillis(), event.getMessage()));
	}

	@Override
	public void onNickChange(NickChangeEvent event) {
		Startup.print("<<NICKCHANGE");
		executor.execute(new TellHandler(null, null, event));
		db.putLastMsg(new LastMsg(event.getUser().getNick(), System.currentTimeMillis(), ""));
	}
}