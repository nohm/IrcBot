package org.snack.irc.worker;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;
import org.snack.irc.settings.Config;

public class DictionaryAPI {

	public static String search(String query) {
		try {
			String searchWord = query.replaceAll("[^\\p{L}| ]", "").toLowerCase();
			String uriWord = URLEncoder.encode(searchWord, "utf-8");

			URL url = new URL("http://api.pearson.com/v2/dictionaries/entries?headword=" + uriWord + "&apikey=f1f4514986ce72d85fb82db55fa4f3c7");
			URLConnection urlConn = url.openConnection();
			urlConn.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(urlConn.getInputStream(), output);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONArray results = jo.getJSONArray("results");
			JSONObject result = results.getJSONObject(0);

			String type = result.getString("part_of_speech");
			JSONArray senses = result.getJSONArray("senses");
			JSONObject sense = senses.getJSONObject(0);
			String definition = sense.getString("definition");

			return Config.speech.get("DE_SUC").replace("<query>", uriWord).replace("<type>", type).replace("<definition>", definition);
		} catch (Exception e) {
			return Config.speech.get("DE_ERR");
		}
	}
}
