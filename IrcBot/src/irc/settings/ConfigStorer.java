package irc.settings;

import irc.model.Bot;
import irc.model.Chan;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Stores the config that's currently loaded
 * 
 * @author snack
 *
 */
public class ConfigStorer {

	/**
	 * Iterates all channels to store them
	 * @throws Exception
	 */
	public static void storeChannels() throws Exception {
		for (String s : Config.channels.keySet()) {
			storeChannel(s);
		}
	}

	/**
	 * Checks for file existance and stores data
	 * @param name the name of the channel
	 * @throws Exception
	 */
	public static void storeChannel(String name) throws Exception {
		if (!new File("./config/" + name).exists()) {
			new File("./config/" + name).mkdir();
			new File("./config/" + name + "/functions.txt").createNewFile();
		}
		storeFunctions(Config.channels.get(name));
		for (Bot b : Config.channels.get(name).bots) {
			if (!new File("./config/" + name + "/bot-" + b.name + ".txt").exists()) {
				new File("./config/" + name + "/bot-" + b.name + ".txt").createNewFile();
			}
			storeBot(name, b);
		}
	}

	/**
	 * Stores the channel functions
	 * @param chan the channel to store
	 * @throws Exception
	 */
	private static void storeFunctions(Chan chan) throws Exception {
		HashMap<String, Boolean> chan_func = chan.def_func;
		HashMap<String, String> chan_def = chan.defaults;
		HashMap<String, Boolean> chan_ign = chan.ignore;
		String data =
				"{\n"+
						"\t\"join\": \""+chan.join+"\",\n"+
						"\t\"mute\": \""+chan.mute+"\",\n"+
						"\t\"functions\": [\n";
		for (Entry<String, Boolean> entry : chan_func.entrySet()) {
			data += "\t\t{ \"" + entry.getKey() + "\": \"" + entry.getValue() + "\" },\n";
		}
		data = data.substring(0, data.length() - 2);
		data += "\n\t],\n\t\"defaults\": [\n";
		for (Entry<String, String> entry : chan_def.entrySet()) {
			data += "\t\t{ \"" + entry.getKey() + "\": \"" + entry.getValue() + "\" },\n";
		}
		data = data.substring(0, data.length() - 2);
		data += "\n\t],\n\t\"ignore\": [\n";
		for (Entry<String, Boolean> entry : chan_ign.entrySet()) {
			data += "\t\t{ \"" + entry.getKey() + "\": \"" + entry.getValue() + "\" },\n";
		}
		data = data.substring(0, data.length() - 2);
		data += "\n\t]\n}";
		TxtWriter.writeTxt("./config/"+chan.name+"/functions.txt", data);
	}

	/**
	 * Stores bot functions
	 * @param chan the channel the bot is from
	 * @param bot the bot to store
	 * @throws Exception
	 */
	private static void storeBot(String chan, Bot bot) throws Exception {
		HashMap<String, Boolean> bot_func = bot.functions;
		String data = "{\n\t\"name\": \""+bot.name+"\",\n\t\"enabled\": \""+bot.enabled+"\",\n\t\"functions\": [\n";
		for (Entry<String, Boolean> entry : bot_func.entrySet()) {
			data += "\t\t{ \"" + entry.getKey() + "\": \"" + entry.getValue() + "\" },\n";
		}
		data = data.substring(0, data.length() - 2);
		data +=	"\n\t]\n}";
		TxtWriter.writeTxt("./config/"+chan+"/bot-"+bot.name+".txt", data);
	}

	/**
	 * Stores the admins
	 * @throws Exception
	 */
	public static void storeAdmins() throws Exception {
		String data = "{\n\t\"admins\": [\n";
		for (Entry<String, String> entry : Config.admins.entrySet()) {
			data += "\t\t{ \"name\": \""+entry.getKey()+"\", \"type\": \""+entry.getValue()+"\" },\n";
		}
		data = data.substring(0, data.length() -2);
		data += "\n\t]\n}";
		TxtWriter.writeTxt("./config/admins.txt", data);
	}
}
