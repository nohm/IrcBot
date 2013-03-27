package org.snack.irc.database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.snack.irc.main.Monitor;
import org.snack.irc.model.LastfmUser;
import org.snack.irc.model.Quote;
import org.snack.irc.model.Tell;
import org.snack.irc.model.WeatherUser;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Main database class which can initialize the database and allow access with
 * static put and get methods.
 * 
 * @author Enrico van Oosten, Bart Hopster
 */
public class DatabaseManager {
	private static final int PORT_NUMBER = 27017;
	private static final String DATABASE_NAME = "db";
	private static final String WEATHER_COLLECTION_NAME = "weathercollection";
	private static final String LASTFM_COLLECTION_NAME = "lastfmcollection";
	private static final String QUOTE_COLLECTION_NAME = "quotecollection";
	private static final String TELL_COLLECTION_NAME = "tellcollection";

	private static final String MONGO_IP = "localhost";

	private Mongo mongo;
	private DB db;
	private DBCollection weather_collection, lastfm_collection, quote_collection, tell_collection;

	private static DatabaseManager instance = null;

	private int tries = 0;

	public static DatabaseManager getInstance() {
		if (instance == null) {
			Monitor.print("Initializing database");
			instance = new DatabaseManager();
			Monitor.print("Initialized database");
		}
		return instance;
	}

	public void initializeConnection() {
		try {
			mongo = new Mongo(MONGO_IP, PORT_NUMBER);
			db = mongo.getDB(DATABASE_NAME);
			weather_collection = db.getCollection(WEATHER_COLLECTION_NAME);
			lastfm_collection = db.getCollection(LASTFM_COLLECTION_NAME);
			quote_collection = db.getCollection(QUOTE_COLLECTION_NAME);
			tell_collection = db.getCollection(TELL_COLLECTION_NAME);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		} catch (MongoException e1) {
			e1.printStackTrace();
		}
	}

	public void closeConnection() {
		mongo.close();
	}

	public void putWeatherUser(WeatherUser user) {
		if (containsWeatherUser(user)) {
			updateWeatherUser(user);
		} else {
			weather_collection.insert(user);
		}
	}

	public void updateWeatherUser(WeatherUser user) {
		BasicDBObject query = new BasicDBObject();
		query.put(WeatherUser.NAME_KEY, user.getName());
		DBCursor cursor = weather_collection.find(query);
		DBObject existing = cursor.next();
		weather_collection.remove(existing);
		existing.put(WeatherUser.LOCATION_KEY, user.getLocation());
		weather_collection.insert(existing);
	}

	public WeatherUser getWeatherUser(String name) {
		BasicDBObject query = new BasicDBObject();
		query.put(WeatherUser.NAME_KEY, name);
		DBCursor cursor = weather_collection.find(query);

		if (cursor.count() == 0) {
			return new WeatherUser("", "");
		}

		try {
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(cursor.next().toString());
			return new WeatherUser(jsonObject.getString(WeatherUser.NAME_KEY), jsonObject.getString(WeatherUser.LOCATION_KEY));
		} catch (JSONException e) {
			return new WeatherUser("", "");
		} finally {
			cursor.close();
		}
	}

	public boolean containsWeatherUser(WeatherUser user) {
		BasicDBObject query = new BasicDBObject();
		query.put(WeatherUser.NAME_KEY, user.getName());
		return weather_collection.count(query) > 0;
	}

	public void putLastfmUser(LastfmUser user) {
		if (containsLastfmUser(user)) {
			updateLastfmUser(user);
		} else {
			lastfm_collection.insert(user);
		}
	}

	public void updateLastfmUser(LastfmUser user) {
		BasicDBObject query = new BasicDBObject();
		query.put(LastfmUser.NAME_KEY, user.getName());
		DBCursor cursor = lastfm_collection.find(query);
		DBObject existing = cursor.next();
		lastfm_collection.remove(existing);
		existing.put(LastfmUser.USERNAME_KEY, user.getUsername());
		lastfm_collection.insert(existing);
	}

	public LastfmUser getLastfmUser(String name) {
		BasicDBObject query = new BasicDBObject();
		query.put(LastfmUser.NAME_KEY, name);
		DBCursor cursor = lastfm_collection.find(query);

		if (cursor.count() == 0) {
			return new LastfmUser("", "");
		}

		try {
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(cursor.next().toString());
			return new LastfmUser(jsonObject.getString(LastfmUser.NAME_KEY), jsonObject.getString(LastfmUser.USERNAME_KEY));
		} catch (JSONException e) {
			return new LastfmUser("", "");
		} finally {
			cursor.close();
		}
	}

	public boolean containsLastfmUser(LastfmUser user) {
		BasicDBObject query = new BasicDBObject();
		query.put(LastfmUser.NAME_KEY, user.getName());
		return lastfm_collection.count(query) > 0;
	}

	public void putQuote(Quote quote) {
		quote_collection.insert(quote);
	}

	public void removeQuote(Quote quote) {
		BasicDBObject query = new BasicDBObject();
		query.put(Quote.CHANNEL_KEY, quote.getChannel());
		query.put(Quote.MESSAGE_KEY, quote.getMessage());
		DBCursor cursor = quote_collection.find(query);
		DBObject existing = cursor.next();
		quote_collection.remove(existing);
	}

	public Quote getQuoteByName(String channel, String name) {
		BasicDBObject query = new BasicDBObject();
		query.put(Quote.CHANNEL_KEY, channel);
		query.put(Quote.NAME_KEY, name);
		DBCursor cursor = quote_collection.find(query);

		if (cursor.count() == 0) {
			return new Quote("", "", "");
		}
		try {
			String next = "";
			if (cursor.count() == 1) {
				next = cursor.next().toString();
			} else {
				int random = new Random().nextInt(cursor.count());
				for (int i = 0; i <= random; i++) {
					next = cursor.next().toString();
				}
			}
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(next);
			tries = 0;
			return new Quote(jsonObject.getString(Quote.CHANNEL_KEY), jsonObject.getString(Quote.NAME_KEY), jsonObject.getString(Quote.MESSAGE_KEY));
		} catch (JSONException e) {
			tries++;
			if (tries <= 3) {
				return getQuoteByName(channel, name);
			} else {
				return new Quote("", "", "");
			}
		} finally {
			cursor.close();
		}
	}

	public Quote getRandomQuote(String channel) {
		BasicDBObject query = new BasicDBObject();
		query.put(Quote.CHANNEL_KEY, channel);
		DBCursor cursor = quote_collection.find();

		if (cursor.count() == 0) {
			return new Quote("", "", "");
		}
		try {
			String next = "";
			if (cursor.count() == 1) {
				next = cursor.next().toString();
			} else {
				int random = new Random().nextInt(cursor.count());
				for (int i = 0; i < random; i++) {
					next = cursor.next().toString();
				}
			}
			JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(next);
			tries = 0;
			return new Quote(jsonObject.getString(Quote.CHANNEL_KEY), jsonObject.getString(Quote.NAME_KEY), jsonObject.getString(Quote.MESSAGE_KEY));
		} catch (JSONException e) {
			tries++;
			if (tries <= 3) {
				return getRandomQuote(channel);
			} else {
				return new Quote("", "", "");
			}
		} finally {
			cursor.close();
		}
	}

	public void putTell(Tell tell) {
		tell_collection.insert(tell);
	}

	public ArrayList<Tell> getTells(String name) {
		BasicDBObject query = new BasicDBObject();
		query.put(Tell.NAME_KEY, name);
		DBCursor cursor = tell_collection.find(query);

		if (cursor.count() == 0) {
			return new ArrayList<Tell>();
		}

		try {
			ArrayList<Tell> tells = new ArrayList<Tell>();
			while (cursor.hasNext()) {
				JSONObject jsonObject = (JSONObject) JSONSerializer.toJSON(cursor.next());
				Tell t = new Tell(jsonObject.getString(Tell.SENDER_KEY), jsonObject.getString(Tell.NAME_KEY), jsonObject.getString(Tell.MESSAGE_KEY));
				tells.add(t);
			}
			return tells;
		} catch (JSONException e) {
			return null;
		} finally {
			cursor.close();
		}
	}

	public void removeTell(Tell tell) {
		BasicDBObject query = new BasicDBObject();
		query.put(Tell.SENDER_KEY, tell.getSender());
		query.put(Tell.NAME_KEY, tell.getName());
		query.put(Tell.MESSAGE_KEY, tell.getMessage());
		DBCursor cursor = tell_collection.find(query);
		DBObject existing = cursor.next();
		tell_collection.remove(existing);
	}
}
