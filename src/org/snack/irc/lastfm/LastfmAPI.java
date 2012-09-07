package org.snack.irc.lastfm;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Calls last.fm for song information for the given username, parses that and returns it.
 * @author snack
 *
 */
public class LastfmAPI {
	/**
	 * Connects with last.fm, parses xml and return given data.
	 * @param username
	 * @return
	 */
    public static String[] getSong(String username) {
    	String data[] = new String[4];
    	try {
	        URL lastfmXml = new URL("http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=" + username + "&limit=1&api_key=97f9215a0928d47eca2b08408f252ba7");
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        Document doc = db.parse(lastfmXml.openStream());
	        doc.getDocumentElement ().normalize ();
	        
	        // Get now playing or not
	        NodeList nodeLst = doc.getElementsByTagName("track");
	        Element nowPlaying = (Element) nodeLst.item(0);
	        String value = nowPlaying.getAttribute("nowplaying");
	        data[0] = value;
	        
	        // Get artist
	        nodeLst = doc.getElementsByTagName("artist");
	        Element artistName = (Element) nodeLst.item(0);
	        String artist = artistName.getTextContent();
	        if (artist.equals("")) {
	        	data[1] = "Unknown Artist";
	        } else {
	        	data[1] = artist;
	        }
	        
	        // Get song
	        nodeLst = doc.getElementsByTagName("name");
	        Element songName = (Element) nodeLst.item(0);
	        String song = songName.getTextContent();
	        if (song.equals("")) {
	        	data[2] = "Unknown Song";
	        } else {
	        	data[2] = song;
	        }
	        
	        // Get album
	        nodeLst = doc.getElementsByTagName("album");
	        Element albumName = (Element) nodeLst.item(0);
	        String album = albumName.getTextContent();
	        if (album.equals("")) {
	        	data[3] = "Unknown Album";
	        } else {
	        	data[3] = album;
	        }
	        
	    }
	    catch(Exception e){
	    	//Unknown username? Return an error.
	    	//e.printStackTrace();
	        data[0] = "No data found for the given name.";
	    }
	    
	    return data;
    }
}
