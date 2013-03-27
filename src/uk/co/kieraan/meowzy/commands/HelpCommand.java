package uk.co.kieraan.meowzy.commands;

import uk.co.kieraan.meowzy.Config;
import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class HelpCommand implements MasterCommand {

    Meowzy bot;
    
    public HelpCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        String response = "Commands: ";
        
        for (MasterCommand command : bot.commands) {
            response += Config.getCommandPrefix() + command.getCommandName() + " ";
        }
        
        bot.sendMessage(channel, response);
    }

}
