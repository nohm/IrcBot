package irc.worker;

import irc.main.Utils;
import irc.model.FThread;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.util.ArrayList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Handles using the 4chan api to get threads
 * 
 * @author snack
 *
 */
public class FourChanAPI {

	/**
	 * Returns all threads on specific board containing the term
	 * @param board the board to search
	 * @param term the term to match
	 * @return a list of found threads
	 * @throws Exception connection error most likely
	 */
	public static FThread[] search(String board, String term) throws Exception {
		ArrayList<FThread> threadList = new ArrayList<FThread>();
		String cleanedTerm = term.toLowerCase();

		ByteArrayOutputStream output = Utils.httpRequest("https://api.4chan.org/" + board.toLowerCase() + "/catalog.json");
		JSONArray jo = (JSONArray) JSONSerializer.toJSON(output.toString().replace("&gt;", ">").replace("&lt;", "<"));

		for (int i = 0; i < jo.size(); i++) {
			JSONObject page = jo.getJSONObject(i);
			JSONArray threads = page.getJSONArray("threads");
			for (int j = 0; j < threads.size(); j++) {
				JSONObject thread = threads.getJSONObject(j);
				FThread parsed = parseThread(board, thread);
				boolean ignore = false; // Ignore sticky/closed threads
				ignore = thread.has("sticky") ? thread.getInt("sticky") == 1 : ignore;
				ignore = thread.has("closed") ? thread.getInt("closed") == 1 : ignore;
				if (!ignore
						&& (parsed.subject.toLowerCase().contains(cleanedTerm) || parsed.subject.toLowerCase().equals(cleanedTerm) || parsed.post.toLowerCase().contains(cleanedTerm)
								|| parsed.post.toLowerCase().equals(cleanedTerm) || parsed.name.toLowerCase().contains(cleanedTerm) || parsed.name.toLowerCase().equals(cleanedTerm))) {
					threadList.add(parsed);
				}
			}
		}
		return threadList.toArray(new FThread[threadList.size()]);
	}

	/**
	 * Returns ONE thread on specific board that is #threadNumber
	 * @param board the board to search
	 * @param threadNumber the thread to search
	 * @return a found thread, or an error
	 * @throws Exception connection error most likely, or nothing found
	 */
	public static FThread retrieve(String board, String threadNumber) throws Exception {
		ByteArrayOutputStream output = Utils.httpRequest("https://api.4chan.org/" + board.toLowerCase() + "/res/" + threadNumber + ".json");
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString().replace("&gt;", ">").replace("&lt;", "<"));

		JSONArray posts = jo.getJSONArray("posts");
		JSONObject thread = posts.getJSONObject(0);
		return parseThread(board, thread);
	}

	/**
	 * Parses the data from json to thread
	 * @param board what board is it, for the url
	 * @param thread the json to parse
	 * @return a parsed FThread
	 * @throws Exception incorrect uri, somehow
	 */
	private static FThread parseThread(String board, JSONObject thread) throws Exception {
		String subject = thread.has("sub") ? Jsoup.clean(thread.getString("sub"), Whitelist.simpleText()).toString() : "n/a";
		String post = thread.has("com") ? Jsoup.clean(thread.getString("com"), Whitelist.simpleText()).toString() : "n/a";
		String number = thread.has("no") ? Jsoup.clean(thread.getString("no"), Whitelist.simpleText()).toString() : "n/a";
		String name = thread.has("name") ? Jsoup.clean(thread.getString("name"), Whitelist.simpleText()).toString() : "n/a";
		String comment = thread.has("com") ? Jsoup.clean(thread.getString("com"), Whitelist.simpleText()).toString() : "n/a";
		String replies = thread.has("replies") ? thread.getString("replies") : "n/a";
		if (comment.length() > 100) {
			comment = comment.substring(0, 96) + "...";
		}
		return new FThread(name, subject, post, new URI("https://boards.4chan.org/" + board.toLowerCase() + "/res/" + number).toString(), comment, replies);
	}
}
