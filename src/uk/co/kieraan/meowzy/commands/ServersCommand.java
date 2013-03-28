package uk.co.kieraan.meowzy.commands;

import java.util.concurrent.TimeoutException;

import com.github.koraktor.steamcondenser.exceptions.SteamCondenserException;
import com.github.koraktor.steamcondenser.steam.servers.SourceServer;

import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class ServersCommand implements MasterCommand {

    Meowzy bot;

    public ServersCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "servers";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        try {
            SourceServer tf4 = new SourceServer("tf4.joe.to");
            tf4.initialize();
            System.out.println(tf4.getServerInfo());
            this.bot.sendMessage(channel, "[TF4] Map: " + tf4.getServerInfo().get("mapName") + "  Players: " + tf4.getServerInfo().get("numberOfPlayers") + " / " + tf4.getServerInfo().get("maxPlayers"));
          } catch (TimeoutException e) {
              this.bot.sendMessage(channel, "[TF4] Error: Timed out.");
              e.printStackTrace();
          } catch (SteamCondenserException e) {
              this.bot.sendMessage(channel, "[TF4] Error: I don't even know what went wrong.");
              e.printStackTrace();
          }
        
        try {
          SourceServer tf5 = new SourceServer("tf5.joe.to");
          tf5.initialize();
          System.out.println(tf5.getServerInfo());
          this.bot.sendMessage(channel, "[TF5] Map: " + tf5.getServerInfo().get("mapName") + "  Players: " + tf5.getServerInfo().get("numberOfPlayers") + " / " + tf5.getServerInfo().get("maxPlayers"));
        } catch (TimeoutException e) {
            this.bot.sendMessage(channel, "[TF5] Error: Timed out.");
            e.printStackTrace();
        } catch (SteamCondenserException e) {
            this.bot.sendMessage(channel, "[TF5] Error: I don't even know what went wrong.");
            e.printStackTrace();
        }

    }

}
