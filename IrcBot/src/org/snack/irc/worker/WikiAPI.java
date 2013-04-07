package org.snack.irc.worker;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.snack.irc.settings.Config;

public class WikiAPI {

	public static String search(String language, String query) {
		try {
			String lang = language.toLowerCase();
			String searchWord = query.replaceAll("[^\\p{L}| ]", "").toLowerCase();
			String uriWord = URLEncoder.encode(searchWord, "utf-8");

			URL url = new URL("https://" + lang + ".wikipedia.org/w/api.php?action=opensearch&limit=1&format=json&search=" + uriWord);
			URLConnection urlConn = url.openConnection();
			urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(urlConn.getInputStream(), output);
			JSONArray jo = (JSONArray) JSONSerializer.toJSON(output.toString());

			JSONArray pages = jo.getJSONArray(1);
			if (pages.size() == 0) {
				return Config.speech.get("WI_NO");
			} else {
				String link = "https://" + lang + ".wikipedia.org/wiki/" + pages.getString(0).replace(" ", "%20");
				return Config.speech.get("WI_SUC").replace("<url>", link);
			}
		} catch (Exception e) {
			return Config.speech.get("WI_ERR");
		}
	}
}
