package org.snack.irc.settings;

import java.io.File;
import java.util.Scanner;

/**
 * Reads data from the txt files with usernames
 * 
 * @author snack
 * 
 */
public class TxtReader {

	/**
	 * Reads a file from path and returns the contents
	 * 
	 * @param path
	 *            Where the file is
	 * @return The file's contents
	 * @throws Exception
	 *             Generic exception
	 */
	public static String parseTxt(String path) throws Exception {
		String contents = "";
		Scanner in = new Scanner(new File(path));
		while (in.hasNextLine()) {
			contents += in.nextLine();
		}
		in.close();
		return contents;
	}
}