package uk.co.kieraan.meowzy.commands;

import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class MeowzyCommand implements MasterCommand {
    
    Meowzy bot;
    
    public MeowzyCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "meowzy";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        bot.sendMessage(channel, "MeowzyCommand");
        
    }

    

}
