package org.snack.irc.main;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

public class Utils {

	/**
	 * Make an http request and return a stream with the response
	 * @param urlString
	 * @return
	 */
	public static ByteArrayOutputStream httpRequest(String urlString) throws Exception {
		URL url = new URL(urlString);
		URLConnection urlConn = url.openConnection();
		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtils.copy(urlConn.getInputStream(), output);
		return output;
	}

	/**
	 * Encode/clean url parameters
	 * @param query
	 * @param clean
	 * @return
	 */
	public static String encodeQuery(String query, boolean clean) {
		String response =  query;
		if (clean) {
			response = query.replaceAll("[^\\p{L}]", "").toLowerCase();
		}
		try {
			return URLEncoder.encode(response, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return query;
		}
	}
}
