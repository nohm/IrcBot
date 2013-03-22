package org.snack.irc.worker;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;

/**
 * Get's the title from a http(s) link, if there is no title it tries to
 * determine the type of file or gives back an errorcode.
 * 
 * @author snack
 * 
 */
public class HtmlGetter {
	public static String getTitle(String urlString) {
		try {
			// Try to get the title
			String title = Jsoup.connect(urlString).timeout(5000).userAgent("Mozilla").get().title();
			title = title.replaceAll("\n", "");
			title = title.replaceAll("  ", " ");
			title = title.replaceAll("  ", " ");
			title = title.replaceAll("  ", " ");
			if (title.length() != 0) {
				if (title.length() > 100) {
					title = title.substring(0, 59) + "...";
				}
				return title;
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			try {
				// Try to determine filesize/type/encoding
				URL url = new URL(urlString);
				URLConnection conn = url.openConnection();
				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
				conn.setReadTimeout(10000);
				int size = conn.getContentLength();
				String sizeString = "B";
				double chosen = 0;
				String output = "";
				if (size <= 0) {
					output = "Unknown size";
				} else {
					double bytes = Double.valueOf(size);
					double kilobytes = (bytes / 1024);
					double megabytes = (kilobytes / 1024);
					double gigabytes = (megabytes / 1024);

					if (kilobytes < 1) {
						sizeString = "B";
						chosen = bytes;
					} else if (megabytes < 1) {
						sizeString = "KiB";
						chosen = kilobytes;
					} else if (gigabytes < 1) {
						sizeString = "MiB";
						chosen = megabytes;
					} else {
						sizeString = "GiB";
						chosen = gigabytes;
					}

					chosen = Double.valueOf(new BigDecimal(chosen).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					output = chosen + " " + sizeString;
				}

				conn.getInputStream().close();
				try {
					conn.getContentEncoding().isEmpty();
					return "[" + conn.getContentType() + ", " + conn.getContentEncoding() + "] " + output;
				} catch (Exception conEx) {
					// conEx.printStackTrace();
					return "[" + conn.getContentType() + "] " + output;
				}
			} catch (Exception ex) {
				// Only got errors? Return it.
				// ex.printStackTrace();
				if (ex.getMessage().contains("403")) {
					return "403 Forbidden";
				} else if (ex.getMessage().contains("404")) {
					return "404 Not Found";
				} else {
					return "No title or file found.";
				}
			}
		}
	}
}
