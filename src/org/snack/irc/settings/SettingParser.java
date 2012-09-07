package org.snack.irc.settings;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.snack.irc.lastfm.LastfmUser;
import org.snack.irc.weather.WeatherUser;


/**
 * Reads data from the txt files with usernames
 * @author snack
 *
 */
public class SettingParser {
	/**
	 * Reads data from the txt file and parses it into WeatherUser objects.
	 * @return parsed users
	 * @throws Exception
	 */
	public static ArrayList<WeatherUser> parseWUsers() throws Exception {
		
		ArrayList<WeatherUser> wUsers = new ArrayList<WeatherUser>();
		
		// Parsed de data uit het txt-bestand
		DataInputStream in = new DataInputStream(new FileInputStream("wUsers.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			if (!strLine.equals("")) {
				if (!strLine.startsWith("#")) {
					String[] s = strLine.split("::");
					
					if (s.length != 1) {
						wUsers.add(new WeatherUser(s[0], s[1]));
					}
				}
			}
		}
		in.close();
		br.close();
		
		return wUsers;
	}
	/**
	 * Reads data from the txt file and parses it into LastfmUser objects.
	 * @return parsed users
	 * @throws Exception
	 */
	public static ArrayList<LastfmUser> parseLUsers() throws Exception {
		
		ArrayList<LastfmUser> lUsers = new ArrayList<LastfmUser>();
		
		// Parsed de data uit het txt-bestand
		DataInputStream in = new DataInputStream(new FileInputStream("lUsers.txt"));
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null)   {
			if (!strLine.startsWith("#")) {
				String[] s = strLine.split("::");
				
				if (s.length != 1) {
					lUsers.add(new LastfmUser(s[0], s[1]));
				}
			}
		}
		in.close();
		br.close();
		
		return lUsers;
	}
}