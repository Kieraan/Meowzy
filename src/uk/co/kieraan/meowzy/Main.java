package uk.co.kieraan.meowzy;

import java.io.File;
import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import uk.co.kieraan.meowzy.util.Log;

public class Main {

    public static void main(String[] args) {

        Meowzy bot = new Meowzy();

        Log.consoleLog("Starting up...");

        Config.loadConfiguration();
        
        File check = new File("meowzy.db");
        if(!check.exists()) {
            bot.sql.resetDatabase();
        }

        try {
            bot.connect(Config.getServerAddress());
        } catch (NickAlreadyInUseException e) {
            if (!Config.getAutoNickChange()) {
                Log.consoleLog("Error", "Could not connect to server: " + Config.getServerAddress());
                e.printStackTrace();
            } else {
                Log.consoleLog("Error", "Nick already in use!");
            }
        } catch (IOException e) {
            Log.consoleLog("Error", "Could not connect to server: " + Config.getServerAddress());
            e.printStackTrace();
        } catch (IrcException e) {
            Log.consoleLog("Error", "Could not connect to server: " + Config.getServerAddress());
            e.printStackTrace();
        }
    }

}
