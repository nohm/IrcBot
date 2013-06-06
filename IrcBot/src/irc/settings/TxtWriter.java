package irc.settings;

import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 * Writes plain text to a file
 * 
 * @author snack
 *
 */
public class TxtWriter {

	/**
	 * Writes plain text to a file
	 * @param path where to write to
	 * @param data what to write
	 * @throws Exception error writing, most likely permissions or unknown location
	 */
	public static void writeTxt(String path, String data) throws Exception {
		FileWriter fstream = new FileWriter(path);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(data); // Close the output stream
		out.close();
	}
}