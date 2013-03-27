package uk.co.kieraan.meowzy;

import java.util.ArrayList;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import uk.co.kieraan.meowzy.commands.*;
import uk.co.kieraan.meowzy.util.*;

public class Meowzy extends PircBot {
    
    public SQL sql = new SQL(this);

    public ArrayList<MasterCommand> commands = new ArrayList<MasterCommand>();
    public boolean authed = false;
    public boolean dontAuth = false;

    public Meowzy() {

        Config.loadConfiguration();

        this.setName(Config.getNick());
        this.setLogin(Config.getLogin());
        this.setVersion(Config.getVersion());
        this.setFinger(Config.getFinger());
        this.setAutoNickChange(Config.getAutoNickChange());
        this.setMessageDelay(Config.getMessageDelay());
        this.setVerbose(Config.getVersboseOutput());

        this.commands.add(new HelpCommand(this));
        //this.commands.add(new MeowzyCommand(this));
        //this.commands.add(new AliasCommand(this));
        this.commands.add(new NoteCommand(this));
    }

    public void onConnect() {
        Log.consoleLog("Connected!");
        authenticate();
    }

    public void authenticate() {
        Log.consoleLog("Authenticating with " + Config.getAuthMethod() + "...");
        if (Config.getAuthMethod().equalsIgnoreCase("NickServ")) {
            identify(Config.getAuthPassword());
        } else if (Config.getAuthMethod().equalsIgnoreCase("AuthServ")) {
            sendMessage("AuthServ@services.gamesurge.net", "AUTH Kieraanbreeze " + Config.getAuthPassword());
        } else {
            Log.consoleLog("Warn", "Unknown authentication method. (" + Config.getAuthMethod() + ")");
            dontAuth = true;
        }
        authed = true;
        joinChannels();
    }

    public void joinChannels() {
        if (!authed && !dontAuth) {
            authenticate();
            return;
        }
        Log.consoleLog("Joining channels...");
        for (String channel : Config.getChannels()) {
            joinChannel(channel);
        }
    }

    public void onJoin(String channel, String sender, String login, String hostname) {
        if(sender.equalsIgnoreCase(getNick())) {
            Log.consoleLog("Info", "Joined channel: " + channel);
            if(Config.getStateVersionOnJoin()) {
                sendMessage(channel, Config.getVersion());
            }
            return;
        }
        
        Log.consoleLog("Join", sender + " just joined " + channel);
        if(Config.getWelcomeUsers()) {
            sendMessage(channel, Config.getWelcomeMessage().replace("<sender>", sender).replace("<channel>", channel));
        }
        
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        String msg = message.toLowerCase();
        String[] msgSplit = msg.split(" ");
        
        // temp for j2
        
        if(msgSplit[0].equalsIgnoreCase("%note") || msgSplit[0].equalsIgnoreCase("!note") && (!channel.equals("#j2dev"))) {
            if(!isInChannel(channel, msgSplit[0])) {
                sendMessage(channel, "I can be used for notes since there isn't a note bot here: .note <user> <message>");
            }
        }
        
        // end temp for j2
        
        if(!sender.equalsIgnoreCase(getNick())) {
            sql.checkNote(channel, sender);
        }

        if (msgSplit[0].startsWith(Config.getCommandPrefix())) {
            String commandName = msgSplit[0].replace(Config.getCommandPrefix(), "");

            for (MasterCommand command : commands) {
                if (commandName.equalsIgnoreCase(command.getCommandName())) {
                    Log.consoleLog("Command", sender + " issued command: " + message);
                    command.exec(channel, sender, commandName, msgSplit, login, hostname, message);
                }
            }
            return;
        }
        
        Log.consoleLog("Message", "<" + channel + "> " + sender + ": " + message);
        
    }
    
    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason)  {
        if(recipientNick.equalsIgnoreCase(getNick())) {
            joinChannel(channel);
        }
    }
    
    public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
        if(targetNick.equalsIgnoreCase(getNick())) {
            if(sourceNick.equalsIgnoreCase("ChanServ")) {
                channel = channel.replace("ChanServ!ChanServ@Services.GameSurge.net INVITE Meowzy ", "");
            }
            Log.consoleLog("Invite", "Being invited to " + channel + " by " + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ")");
            if(sourceHostname.equalsIgnoreCase("irc.kieraan.co.uk") || channel.contains("#j2dev")) {
                Log.consoleLog("Invite", "Accepting invite");
                joinChannel(channel);
            }
        }
    }
    
    public void reConnect() {
        try {
            reconnect();
        }
        catch (Exception e) {
            try {
                Thread.sleep(10000);
                reConnect();
            }
            catch (Exception anye) {
                // Fail
            }
        }
    }
    
    public void onDisconnect() {
        while (!isConnected()) {
           authed = false;
           dontAuth = false;
           reConnect();
        }
    }
    
    // temp
    
    public boolean isInChannel(String channel, String bot) {
        for(User user : getUsers(channel)) {
            if(bot.equalsIgnoreCase("%note") && user.getNick().equalsIgnoreCase("Kitteh")) {
                return true;
            }
            if(bot.equalsIgnoreCase("!note") && user.getNick().equalsIgnoreCase("benderj2")) {
                return true;
            }
            if(bot.equalsIgnoreCase(".note")) {
                if(user.getNick().equalsIgnoreCase("Kitteh") || user.getNick().equalsIgnoreCase("benderj2")) {
                    return true;
                }
            }
            
        }
        return false;
    }

}
