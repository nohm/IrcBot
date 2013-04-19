package irc.main;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
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
		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.2; WOW64; rv:22.0) Gecko/20130401 Firefox/22.0");
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

	/**
	 * Returns a double with two decimals
	 * @param d, the double to convert
	 * @return the converted double
	 */
	public static double twoDecimalsDouble(double d) {
		return new BigDecimal(d).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * Returns a String with two decimals
	 * @param d, the double to convert
	 * @return the converted double as a String
	 */
	public static String twoDecimalsString(double d) {
		return new BigDecimal(d).setScale(2,BigDecimal.ROUND_HALF_UP).toString();
	}
}
