package org.snack.irc.model;

import com.mongodb.BasicDBObject;

public class WeatherUser extends BasicDBObject {
	private static final long serialVersionUID = 3877490741872271987L;

	public static final String NAME_KEY = "Name";
	public static final String LOCATION_KEY = "Location";

	public WeatherUser(String name, String location) {
		super(2);
		put(NAME_KEY, name);
		put(LOCATION_KEY, location);
	}

	public String getName() {
		return (String) get(NAME_KEY);
	}

	public String getLocation() {
		return (String) get(LOCATION_KEY);
	}
}
