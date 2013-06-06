package irc.main;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.apache.commons.io.IOUtils;

/**
 * Utility functions that are used at many places.
 * 
 * @author snack
 *
 */
public class Utils {

	/**
	 * Make an http request and return a stream with the response
	 * @param urlString the url to get data from
	 * @return the found data
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
	 * @param query what to encode/clean
	 * @param clean should it be cleaned
	 * @return the encoded/cleaned query
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

	public static String decimalsString(int dec, double d) {
		return new BigDecimal(d).setScale(dec,BigDecimal.ROUND_HALF_UP).toString();
	}

	public static double round(int dec, double d) {
		return new BigDecimal(d).setScale(dec,BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * Convert milliseconds to readable time
	 * @param millis
	 * @return
	 */
	public static String getInHMS(long millis) {
		long diffInDays  = millis/1000/86400;
		long diffInHours = (millis/1000 - 86400*diffInDays) / 3600;
		long diffInMins  = (millis/1000 - 86400*diffInDays - 3600*diffInHours) / 60;
		long diffInSecs  = (millis/1000 - 86400*diffInDays - 3600*diffInHours - 60*diffInMins);
		String hourStr = diffInHours < 10 ? "0" + diffInHours : "" + diffInHours;
		String minStr = diffInMins < 10 ? "0" + diffInMins : "" + diffInMins;
		String secStr = diffInSecs < 10 ? "0" + diffInSecs : "" + diffInSecs;
		if (diffInDays == 0) {
			if (diffInHours == 0) {
				return minStr + ":" + secStr;
			}
			return hourStr + ":" + minStr + ":" + secStr;
		}
		return diffInDays + " days " + hourStr + ":" + minStr + ":" + secStr;
	}
}
