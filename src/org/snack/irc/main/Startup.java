package org.snack.irc.main;
import org.pircbotx.PircBotX;

/**
 * Starts the bot up, gives it all the info it needs, adds an event handler and mkes it connect.
 * @author snack
 * 
 */
public class Startup {
    public static void main(String[] args) throws Exception {
    	//Setup a new bot
    	PircBotX bot = new PircBotX();
    	
    	//Fill in it's name, login, etc.
    	bot.setName("SnackBot");
    	bot.setLogin("ItsaBot");
    	bot.setVersion("MaybeItllCrashLess");

    	//Toggle debugging
        bot.setVerbose(true);
        //bot.setVerbose(false);
        
        //Give the bot a listener
        bot.getListenerManager().addListener(new SnackBot());

        //Connect to a server & channel
        bot.connect("irc.rizon.net");
        
        bot.sendRawLine("NICKSERV IDENTIFY 1.pieps");
        //bot.sendRawLine("JOIN #pantsumen");
        bot.sendRawLine("JOIN #snacktest");
        //bot.sendRawLine("JOIN #A.S-spamm.section");
    } 
}