package org.snack.irc.weather;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Calls google for weather information for the given location, parses that and returns it.
 * @author snack
 *
 */
public class WeatherAPI {

	/**
	 * Connect with google, parses xml and returns the given data
	 * @param location
	 * @return data
	 */
	public static final String[] getWeather(String location){
	    String data[] = new String[6];
	    try {
	    	String fixedLoc = location.replaceAll(" ", "+");
	    	fixedLoc = fixedLoc.substring(0, 1).toUpperCase() + fixedLoc.substring(1);
	        URL googleWeatherXml = new URL("http://api.wunderground.com/api/bf6fcb121000e936/geolookup/conditions/q/" + fixedLoc + ".xml");
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder(); 
	        Document doc = db.parse(googleWeatherXml.openStream());
	        
	        // Get City
	        NodeList nodeLst = doc.getElementsByTagName("city");
	        Element birthTime = (Element) nodeLst.item(0);
	        nodeLst = doc.getElementsByTagName("country_name");
	        Element countryName = (Element) nodeLst.item(0);
	        String value = birthTime.getTextContent() + ", " + countryName.getTextContent();
	        data[0] = value;
	 
	        // Get Condition
	        nodeLst = doc.getElementsByTagName("weather");
	        Element curCond = (Element) nodeLst.item(0);
	        String curCondStr = curCond.getTextContent();
	       	data[1] = curCondStr;
	
	        // Get Temp C
	        nodeLst = doc.getElementsByTagName("temp_c");
	        Element curTempC = (Element) nodeLst.item(0);
	        String curTempCStr = curTempC.getTextContent();
	        data[2] = curTempCStr;
	        
	        // Get Temp F
	        nodeLst = doc.getElementsByTagName("temp_f");
	        Element curTempF = (Element) nodeLst.item(0);
	        String curTempFStr = curTempF.getTextContent();
	        data[3] = curTempFStr;
	        
	        if (countryName.getTextContent().equals("USA")) {
		        // Get Wind
		        nodeLst = doc.getElementsByTagName("wind_dir");
		        Element curWindDir = (Element) nodeLst.item(0);
		        nodeLst = doc.getElementsByTagName("wind_mph");
		        Element curWindMph = (Element) nodeLst.item(0);
		        nodeLst = doc.getElementsByTagName("wind_gust_mph");
		        Element curWindGustMph = (Element) nodeLst.item(0);
		        String curWindStr = curWindDir.getTextContent() + " at " + curWindMph.getTextContent() + "MPH gusting to " + curWindGustMph.getTextContent() + "MPH";
		        data[4] = curWindStr;
	        } else {
		        // Get Wind
		        nodeLst = doc.getElementsByTagName("wind_dir");
		        Element curWindDir = (Element) nodeLst.item(0);
		        nodeLst = doc.getElementsByTagName("wind_kph");
		        Element curWindKph = (Element) nodeLst.item(0);
		        nodeLst = doc.getElementsByTagName("wind_gust_kph");
		        Element curWindGustKph = (Element) nodeLst.item(0);
		        String curWindStr = curWindDir.getTextContent() + " at " + curWindKph.getTextContent() + "KPH gusting to " + curWindGustKph.getTextContent() + "KPH";
		        data[4] = curWindStr;
	        }
	        
	        // Get Humidity
	        nodeLst = doc.getElementsByTagName("relative_humidity");
	        Element curHumidity = (Element) nodeLst.item(0);
	        String curHumidityStr = curHumidity.getTextContent();
	        data[5] = curHumidityStr;
	        
	        return data;
	    }
	    catch(Exception e){
	    	//ex.printStackTrace();
	    	data[0] = "No data found for the given location, or too many request made to the API.";
	        return data;
	    }
	}
}
