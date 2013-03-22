package org.snack.irc.worker;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.commons.io.IOUtils;

/**
 * Calls last.fm for song information for the given username, parses that and
 * returns it.
 * 
 * @author snack
 * 
 */
public class LastfmAPI {

	/**
	 * Last.fm API keys. Your API Key is 97f9215a0928d47eca2b08408f252ba7 Your
	 * secret is 74071ee6cf4270a5a7b0ad8b7150eb4c2
	 */

	/**
	 * Connects with last.fm, parses xml and return given data.
	 * 
	 * @param username
	 * @return
	 */
	public static String[] getSong(String username) {
		String data[] = new String[4];
		try {
			URL url = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=" + username + "&limit=1&api_key=97f9215a0928d47eca2b08408f252ba7&format=json");
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			IOUtils.copy(url.openStream(), output);

			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONObject recenttracks = jo.getJSONObject("recenttracks");
			JSONArray tracks = recenttracks.getJSONArray("track");
			JSONObject track = tracks.getJSONObject(0);
			// Now playing
			JSONObject attr = track.getJSONObject("@attr");
			data[0] = attr.getString("nowplaying");
			// Artist
			JSONObject artist = track.getJSONObject("artist");
			String artist_data = artist.getString("#text");
			data[1] = artist_data.equals("") ? "Unknown Artist" : artist_data;
			// Song
			String song = track.getString("name");
			data[2] = song.equals("") ? "Unknown Song" : song;
			// Album
			JSONObject album = track.getJSONObject("album");
			String album_data = album.getString("#text");
			data[3] = album_data.equals("") ? "Unknown Album" : album_data;
		} catch (Exception e) {
			// Unknown username? Return an error.
			e.printStackTrace();
			data[0] = "No data found for the given name.";
		}
		return data;
	}
}
