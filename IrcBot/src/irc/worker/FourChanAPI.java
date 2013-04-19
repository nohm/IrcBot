package irc.worker;

import irc.main.Utils;
import irc.model.FThread;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * TODO Documentation Config Integration Handler Format
 * 
 */

public class FourChanAPI {

	public static FThread retrieve(String board, String threadNumber) throws Exception {
		ByteArrayOutputStream output = Utils.httpRequest("https://api.4chan.org/" + board.toLowerCase() + "/res/" + threadNumber + ".json");
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString().replace("&gt;", ">").replace("&lt;", "<"));

		JSONArray posts = jo.getJSONArray("posts");
		JSONObject thread = posts.getJSONObject(0);
		String subject;
		String post;
		String number;
		String name;
		String comment;
		String replies;
		try {
			subject = Jsoup.clean(thread.getString("sub"), Whitelist.simpleText()).toString();
			subject = subject.replace("&gt;", ">").replace("&lt;", "<");
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
		try {
			comment = Jsoup.clean(thread.getString("com"), Whitelist.simpleText()).toString();
			comment = comment.replace("&gt;", ">").replace("&lt;", "<");
			if (comment.length() > 100) {
				comment = comment.substring(0, 96) + "...";
			}
		} catch (JSONException e) {
			comment = "n/a";
		}
		try {
			replies = thread.getString("replies");
		} catch (JSONException e) {
			replies = "n/a";
		}
		return new FThread(name, subject, post, new URI("https://boards.4chan.org/" + board.toLowerCase() + "/res/" + number).toString(), comment, replies);
	}

	public static FThread[] search(String board, String term) throws Exception {
		ArrayList<FThread> threadList = new ArrayList<FThread>();
		String cleanedTerm = term.toLowerCase();//Utils.encodeQuery(term, true);

		ByteArrayOutputStream output = Utils.httpRequest("https://api.4chan.org/" + board.toLowerCase() + "/catalog.json");
		JSONArray jo = (JSONArray) JSONSerializer.toJSON(output.toString().replace("&gt;", ">").replace("&lt;", "<"));

		for (int i = 0; i < jo.size(); i++) {
			JSONObject page = jo.getJSONObject(i);
			JSONArray threads = page.getJSONArray("threads");
			for (int j = 0; j < threads.size(); j++) {
				JSONObject thread = threads.getJSONObject(j);
				String subject;
				String post;
				String number;
				String name;
				String comment;
				String replies;
				boolean ignore = false; // Ignore sticky/closed threads
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
				try {
					comment = Jsoup.clean(thread.getString("com"), Whitelist.simpleText()).toString();
					if (comment.length() > 100) {
						comment = comment.substring(0, 96) + "...";
					}
				} catch (JSONException e) {
					comment = "n/a";
				}
				try {
					replies = thread.getString("replies");
				} catch (JSONException e) {
					replies = "n/a";
				}
				try {
					ignore = thread.getInt("sticky") == 1;
				} catch (JSONException e) {
				}
				try {
					ignore = thread.getInt("closed") == 1;
				} catch (JSONException e) {
				}
				if (!ignore
						&& (subject.toLowerCase().contains(cleanedTerm) || subject.toLowerCase().equals(cleanedTerm) || post.toLowerCase().contains(cleanedTerm)
								|| post.toLowerCase().equals(cleanedTerm) || name.toLowerCase().contains(cleanedTerm) || name.toLowerCase().equals(cleanedTerm))) {
					threadList.add(new FThread(name, subject, post, new URI("https://boards.4chan.org/" + board.toLowerCase() + "/res/" + number).toString(), comment, replies));
				}
			}
		}

		return threadList.toArray(new FThread[threadList.size()]);
	}
}
