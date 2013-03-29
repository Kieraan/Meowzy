package uk.co.kieraan.meowzy;

import java.sql.*;

import uk.co.kieraan.meowzy.util.Log;
import uk.co.kieraan.meowzy.util.Util;

public class SQL {
    
    Meowzy bot;

    public SQL(Meowzy bot) {
        this.bot = bot;
    }

    Connection connection;

    public void sqlConnect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:meowzy.db");
        } catch (ClassNotFoundException e) {
            Log.consoleLog("Error", "Could not connect to database. (ClassNotFoundException)");
            e.printStackTrace();
        } catch (SQLException e) {
            Log.consoleLog("Error", "Could not connect to database. (SQLException)");
            e.printStackTrace();
        }
    }

    public void sqlDisconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            Log.consoleLog("Error", "Could not close connection to database.");
            e.printStackTrace();
        }
    }
    
    public void resetDatabase() {
        sqlConnect();
        
        Log.consoleLog("Resetting database file");
        
        Statement statement;
        
        try {
            statement = connection.createStatement();
            statement.executeUpdate("DROP TABLE IF EXISTS aliases;");
            statement.executeUpdate("DROP TABLE IF EXISTS notes;");
            statement.executeUpdate("DROP TABLE IF EXISTS accesslist;");
            statement.executeUpdate("CREATE TABLE aliases (alias, response, creator);");
            statement.executeUpdate("CREATE TABLE accesslist (nick, hostname, level);");
            statement.executeUpdate("CREATE TABLE notes (sender, receiver, message, channel);");
        } catch (SQLException e) {
            Log.consoleLog("Error", "Failed to reset database.");
            e.printStackTrace();
        }
        
        sqlDisconnect();
    }
    
    public void checkNote(String channel, String sender) {
        sqlConnect();
        
        Statement statement;
        boolean noteSent = false;
        
        try {
            statement = connection.createStatement();
            Statement deleteStatement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM notes WHERE receiver = '" + sender + "';"); // AND channel = '" + channel + "'
            String finalMessage = "";
            while (results.next()){
                if (sender.equalsIgnoreCase(results.getString("receiver"))) {
    
                    if (!noteSent) {
                        finalMessage += sender + ", you have notes: ";
                        Log.consoleLog("Giving " + sender + " their notes.");
                        noteSent = true;
                    }
                    // This is probably stupid but woo who cares :D
                    String message_toSend = results.getString("message").toString().replace("<BACKSLASH>", "\\").replace("<APOSTROPHE>", "'");
                    finalMessage += "<" + results.getString("sender") + "> " + message_toSend + "  ";
                    
                    deleteStatement.executeUpdate("DELETE FROM notes WHERE message = '" + results.getString("message") + "' AND channel = '" + channel + "'");

                }
            }
            if(noteSent) {
                this.bot.sendMessage(channel, finalMessage);
            }
        } catch (SQLException e) {
            Log.consoleLog("Error", "Failed to check for notes.");
            e.printStackTrace();
        }
        
        sqlDisconnect();
    }
    
    public void addNote(String channel, String sender, String message) {
        String[] split = message.split(" ");
        
        if(message.equalsIgnoreCase(Config.getCommandPrefix() + "note")) {
            this.bot.sendMessage(channel, "No receiver specified, please try again.");
            return;
        } 
        
        if(split[1].equalsIgnoreCase(this.bot.getNick())) {
            this.bot.sendMessage(channel, "I don't take notes...");
            return;
        }
        
        if(message.equalsIgnoreCase(split[0] + " " + split[1])) {
            this.bot.sendMessage(channel, "You didn't give me a message to store, please try again.");
            return;
        }
        
        sqlConnect();
        
        // This is probably stupid but woo who cares :D
        String noteToDB = Util.combineSplit(2, split, " ").replace("'", "<APOSTROPHE>").replace("\\", "<BACKSLASH>");
        Statement statement;
        
        try {
            statement = connection.createStatement();
            statement.executeUpdate("INSERT INTO notes (sender, receiver, message, channel) VALUES ('" + sender + "','" + split[1] + "','" + noteToDB + "','" + channel + "')");
            this.bot.sendMessage(channel, "Note Stored.");
            Log.consoleLog("Storing note from: " + sender);
        } catch (SQLException e) {
            Log.consoleLog("Error", "Failed to check for notes.");
            this.bot.sendMessage(channel, "Woops, I broke something, try again.");
            e.printStackTrace();
        } 
        
        sqlDisconnect();
    }

}
