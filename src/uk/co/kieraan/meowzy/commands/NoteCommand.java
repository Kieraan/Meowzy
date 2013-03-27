package uk.co.kieraan.meowzy.commands;

import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class NoteCommand implements MasterCommand {

    Meowzy bot;

    public NoteCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "note";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        if(this.bot.isInChannel(channel, args[0])) {
            this.bot.sendMessage(channel, "There's a proper bot in the channel, use them instead!");
            return;
        }
        bot.sql.addNote(channel, sender, message);
    }

}
