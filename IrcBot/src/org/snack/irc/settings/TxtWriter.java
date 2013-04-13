package org.snack.irc.settings;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class TxtWriter {

	public static void writeTxt(String path, String data) throws Exception {
		FileWriter fstream = new FileWriter(path);
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(data); // Close the output stream
		out.close();
	}
}