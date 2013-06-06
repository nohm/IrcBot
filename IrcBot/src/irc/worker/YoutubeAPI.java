package irc.worker;

import irc.main.Utils;

import java.io.ByteArrayOutputStream;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class YoutubeAPI {

	public static String search(String input) {
		String response;
		try {
			String query = Utils.encodeQuery(input.substring(input.indexOf(" ") + 1), true);
			ByteArrayOutputStream output = Utils.httpRequest("http://gdata.youtube.com/feeds/api/videos?v=2&alt=jsonc&max-results=1&q=" + query);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
			JSONObject data = jo.getJSONObject("data");

			if (jo.containsKey("error")) {
				response = "Error performing search :(";
			} else if (data.getInt("totalItems") == 0) {
				response = "No results found :(";
			} else {
				String videoId = data.getJSONArray("items").getJSONObject(0).getString("id");
				response = getVideoDescription(videoId) + " - http://youtu.be/" + videoId;
			}
		} catch (Exception e) {
			// e.printStackTrace();
			response = "Error retrieving info :(";
		}
		return response;
	}

	public static String getVideoDescription(String id) throws Exception {
		ByteArrayOutputStream output = Utils.httpRequest("http://gdata.youtube.com/feeds/api/videos/?v=2&alt=jsonc&q=" + id);
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(output.toString());
		JSONObject data = jo.getJSONObject("data");

		String info;
		if (jo.containsKey("error")) {
			throw new Exception();
		} else {
			JSONObject video = data.getJSONArray("items").getJSONObject(0);
			info = video.getString("title");
			if (!video.containsKey("duration")) {
				return info;
			}
			info += " - length " + Utils.getInHMS(video.getInt("duration") * 1000);
			if (video.containsKey("rating")) {
				info += " - rated " + Utils.decimalsString(1, video.getDouble("rating")) + "/5.0 (" + video.getInt("ratingCount") + ")";
			}
			if (video.containsKey("viewCount")) {
				info += " - " + video.getInt("viewCount") + " views";
			}
			info += " - " + video.getString("uploader") + " on " + video.getString("uploaded").split("T")[0].replace("-", ".");
			if (video.containsKey("contentRating")) {
				info += " - NSFW";
			}
		}
		return info;
	}
}
