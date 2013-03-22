package org.snack.irc.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import org.snack.irc.model.LastfmUser;
import org.snack.irc.model.Tell;
import org.snack.irc.model.WeatherUser;

/**
 * Reads data from ttxt files
 * 
 * @author snack
 * 
 */
public class SettingParser {

	/*
	 * Reads txt files and returns them as string array
	 * 
	 * @param path The path where the file is
	 */
	public static String[] parseTxt(String path) throws Exception {
		ArrayList<String> parsed = new ArrayList<String>();
		Scanner in = new Scanner(new File(path));
		while (in.hasNextLine()) {
			parsed.add(in.nextLine());
		}
		in.close();

		return parsed.toArray(new String[parsed.size()]);
	}

	/**
	 * Reads data from the txt file and parses it into WeatherUser objects.
	 * 
	 * @return parsed users
	 * @throws Exception
	 *             Generic exception
	 */
	public static ArrayList<WeatherUser> parseWUsers() throws Exception {
		String[] parsed = parseTxt(Configuration.SAVE_LOC + "wUsers.txt");
		ArrayList<WeatherUser> wUsers = new ArrayList<WeatherUser>();

		for (String strLine : parsed) {
			if (!strLine.equals("")) {
				if (!strLine.startsWith("#")) {
					String[] s = strLine.split("::");

					if (s.length != 1) {
						wUsers.add(new WeatherUser(s[0], s[1]));
					}
				}
			}
		}

		return wUsers;
	}

	/**
	 * Reads data from the txt file and parses it into LastfmUser objects.
	 * 
	 * @return parsed users
	 * @throws Exception
	 *             Generic exception
	 */
	public static ArrayList<LastfmUser> parseLUsers() throws Exception {
		String[] parsed = parseTxt(Configuration.SAVE_LOC + "lUsers.txt");
		ArrayList<LastfmUser> lUsers = new ArrayList<LastfmUser>();

		for (String strLine : parsed) {
			if (!strLine.startsWith("#")) {
				String[] s = strLine.split("::");

				if (s.length != 1) {
					lUsers.add(new LastfmUser(s[0], s[1]));
				}
			}
		}

		return lUsers;
	}

	/**
	 * Reads data from the txt file and parses it into string objects.
	 * 
	 * @return parsed quotes
	 * @throws Exception
	 *             Generic exception
	 */
	public static ArrayList<String> parseQuotes() throws Exception {
		String[] parsed = parseTxt(Configuration.SAVE_LOC + "quotes.txt");
		ArrayList<String> quotes = new ArrayList<String>();

		for (String strLine : parsed) {
			if (!strLine.startsWith("#")) {
				quotes.add(strLine);
			}
		}

		return quotes;
	}

	/**
	 * Reads data from the txt file and parses it into Tell objects.
	 * 
	 * @return parsed tells
	 * @throws Exception
	 *             Generic exception
	 */
	public static ArrayList<Tell> parseTells() throws Exception {
		String[] parsed = parseTxt(Configuration.SAVE_LOC + "tell.txt");
		ArrayList<Tell> tells = new ArrayList<Tell>();

		for (String strLine : parsed) {
			if (!strLine.startsWith("#")) {
				String[] s = strLine.split("::");

				if (s.length == 3) {
					tells.add(new Tell(s[0], s[1], s[2]));
				}
			}
		}

		return tells;
	}
}