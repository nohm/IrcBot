package org.snack.irc.settings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import org.snack.irc.model.LastfmUser;
import org.snack.irc.model.Tell;
import org.snack.irc.model.WeatherUser;

/**
 * Stores the usernames to a txt file
 * 
 * @author snack
 * 
 */
public class SettingStorer {
	/**
	 * Makes a file, and stores every users name & location on a line.
	 * 
	 * @param wUsers
	 * @throws Exception
	 */
	public static void storeWUsers(ArrayList<WeatherUser> wUsers) throws Exception {

		FileWriter fstream = new FileWriter(Configuration.SAVE_LOC + "wUsers.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for (WeatherUser user : wUsers) {
			out.write(user.getName() + "::" + user.getLocation());
			out.newLine();
		}
		out.close();
	}

	/**
	 * Makes a file, and stores every users name & nick on a line.
	 * 
	 * @param lUsers
	 * @throws Exception
	 */
	public static void storeLUsers(ArrayList<LastfmUser> lUsers) throws Exception {

		FileWriter fstream = new FileWriter(Configuration.SAVE_LOC + "lUsers.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for (LastfmUser user : lUsers) {
			out.write(user.getName() + "::" + user.getUsername());
			out.newLine();
		}
		out.close();
	}

	// TODO: Docs
	public static void storeQuotes(ArrayList<String> quotes) throws Exception {

		FileWriter fstream = new FileWriter(Configuration.SAVE_LOC + "quotes.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for (String quote : quotes) {
			out.write(quote);
			out.newLine();
		}
		out.close();
	}

	// TODO: Docs
	public static void storeTells(ArrayList<Tell> tells) throws Exception {

		FileWriter fstream = new FileWriter(Configuration.SAVE_LOC + "tell.txt");
		BufferedWriter out = new BufferedWriter(fstream);
		for (Tell tell : tells) {
			out.write(tell.getName() + "::" + tell.getSender() + "::" + tell.getMessage());
			out.newLine();
		}
		out.close();
	}
}