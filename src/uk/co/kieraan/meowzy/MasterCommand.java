package uk.co.kieraan.meowzy;

public interface MasterCommand {

    public String getCommandName();

    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message);

}
