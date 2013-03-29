package uk.co.kieraan.meowzy.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class FMLCommand implements MasterCommand {

    Meowzy bot;

    public FMLCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "fml";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        String fmlMessage = "";
        try {
            URL url = new URL("http://rscript.org/lookup.php?type=fml");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((fmlMessage = bReader.readLine()) != null) {
                if (fmlMessage.startsWith("TEXT: ")) {
                    fmlMessage = fmlMessage.replace("TEXT: ", "");
                    break;
                } 
            }
        } catch (IOException ex) {
            if (ex.getMessage().contains("503")) {
                this.bot.sendMessage(channel, "Error: 503");
            }
            if (ex.getMessage().contains("404")) {
                this.bot.sendMessage(channel, "Error: 503");
            }
        }
        this.bot.sendMessage(channel, "[FML] " + fmlMessage);
    }

}
