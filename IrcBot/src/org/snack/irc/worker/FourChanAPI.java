package org.snack.irc.worker;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.snack.irc.model.FThread;

/**
 * TODO Documentation Config Integration Handler Format
 * 
 */

public class FourChanAPI {

	public static FThread[] search(String board, String term) throws Exception {
		ArrayList<FThread> threadList = new ArrayList<FThread>();
		String cleanedTerm = term.replaceAll("[^\\p{L}| ]", "").toLowerCase();

		URL url = new URL("https://api.4chan.org/" + board.toLowerCase() + "/catalog.json");
		URLConnection urlConn = url.openConnection();
		urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		IOUtils.copy(urlConn.getInputStream(), output);
		JSONArray jo = (JSONArray) JSONSerializer.toJSON(output.toString());

		for (int i = 0; i < jo.size(); i++) {
			JSONObject page = jo.getJSONObject(i);
			JSONArray threads = page.getJSONArray("threads");
			for (int j = 0; j < threads.size(); j++) {
				JSONObject thread = threads.getJSONObject(j);
				String subject;
				String post;
				String number;
				String name;
				try {
					subject = Jsoup.clean(thread.getString("sub"), Whitelist.simpleText()).toString();
				} catch (JSONException e) {
					subject = "n/a";
				}
				try {
					post = Jsoup.clean(thread.getString("com"), Whitelist.simpleText()).toString();
				} catch (JSONException e) {
					post = "n/a";
				}
				try {
					number = Jsoup.clean(thread.getString("no"), Whitelist.simpleText()).toString();
				} catch (JSONException e) {
					number = "n/a";
				}
				try {
					name = Jsoup.clean(thread.getString("name"), Whitelist.simpleText()).toString();
				} catch (JSONException e) {
					name = "n/a";
				}
				if (subject.toLowerCase().contains(cleanedTerm) || subject.toLowerCase().equals(cleanedTerm) || post.toLowerCase().contains(cleanedTerm)
						|| post.toLowerCase().equals(cleanedTerm) || name.toLowerCase().contains(cleanedTerm) || name.toLowerCase().equals(cleanedTerm)) {
					threadList.add(new FThread(name, subject, post, new URI("https://boards.4chan.org/" + board.toLowerCase() + "/res/" + number).toString()));
				}
			}
		}

		return threadList.toArray(new FThread[threadList.size()]);
	}
}
