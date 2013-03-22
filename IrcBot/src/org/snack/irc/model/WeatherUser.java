package org.snack.irc.model;

/**
 * Simply represents someone that stored an weather location
 * @author snack
 *
 */
public class WeatherUser {
	
	private String name;
	private String location;

	public WeatherUser(String name, String location) {
		this.name = name;
		this.location = location;
	}
		
	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}
}
