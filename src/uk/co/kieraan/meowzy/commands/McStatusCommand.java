package uk.co.kieraan.meowzy.commands;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.jibble.pircbot.Colors;

import uk.co.kieraan.meowzy.MasterCommand;
import uk.co.kieraan.meowzy.Meowzy;

public class McStatusCommand implements MasterCommand {

    Meowzy bot;

    public McStatusCommand(Meowzy bot) {
        this.bot = bot;
    }

    @Override
    public String getCommandName() {
        return "mcstatus";
    }

    @Override
    public void exec(String channel, String sender, String commandName, String[] args, String login, String hostname, String message) {
        String[] toSend = {};
        String statusMessage = "";
        try {
            URL url = new URL("http://status.mojang.com/check");
            BufferedReader bReader = new BufferedReader(new InputStreamReader(url.openStream()));
            if ((statusMessage = bReader.readLine()) != null) {
                statusMessage = statusMessage.replace("green", Colors.GREEN + "Online" + Colors.NORMAL);
                statusMessage = statusMessage.replace("red", Colors.RED + "Offline" + Colors.NORMAL);
                statusMessage = statusMessage.replace("{", "").replace("}", "").replace("[", "").replace("]", "").replace("\"", "");
                statusMessage = statusMessage.replace("login.minecraft.net", "Minecraft Login");
                statusMessage = statusMessage.replace("session.minecraft.net", "Minecraft Sessions");
                statusMessage = statusMessage.replace("account.mojang.com", "Mojang Accounts");
                statusMessage = statusMessage.replace("auth.mojang.com", "Mojang Auth");
                statusMessage = statusMessage.replace("skins.minecraft.net", "Minecraft Skins");;
                statusMessage = statusMessage.replace("minecraft.net", "Minecraft Website");
                toSend = statusMessage.split(",");
            }
        } catch (IOException ex) {
            if (ex.getMessage().contains("503")) {
                this.bot.sendMessage(channel, "Error: 503");
            }
            if (ex.getMessage().contains("404")) {
                this.bot.sendMessage(channel, "Error: 503");
            }
        }
        for (int i = 0; i < toSend.length; i++) {
            this.bot.sendMessage(channel, "[McStatus] " + toSend[i]);
        }
    }
}
