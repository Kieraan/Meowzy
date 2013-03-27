package uk.co.kieraan.meowzy.commands;

import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class AliasCommand implements MasterCommand {

    Meowzy bot;

    public AliasCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "alias";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        bot.sendMessage(channel, "AliasCommand");

    }

}
