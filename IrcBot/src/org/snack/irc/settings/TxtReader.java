package org.snack.irc.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Reads data from the txt files with usernames
 * 
 * @author snack
 * 
 */
public class TxtReader {

	// TODO: docs
	public static String[] parseTxt(String path) throws Exception {
		ArrayList<String> parsed = new ArrayList<String>();
		// Parsed de data uit het txt-bestand
		Scanner in = new Scanner(new File(path));
		while (in.hasNextLine()) {
			parsed.add(in.nextLine());
		}
		in.close();

		return parsed.toArray(new String[parsed.size()]);
	}
}