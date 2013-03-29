package uk.co.kieraan.meowzy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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
        this.commands.add(new ServersCommand(this));
        this.commands.add(new McStatusCommand(this));
        this.commands.add(new FMLCommand(this));
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
        if (sender.equalsIgnoreCase(getNick())) {
            Log.consoleLog("Info", "Joined channel: " + channel);
            if (Config.getStateVersionOnJoin()) {
                sendMessage(channel, Config.getVersion());
            }
            return;
        }

        Log.consoleLog("Join", sender + " just joined " + channel);
        if (Config.getWelcomeUsers()) {
            sendMessage(channel, Config.getWelcomeMessage().replace("<sender>", sender).replace("<channel>", channel));
        }

    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        String msg = message.toLowerCase();
        String[] msgSplit = msg.split(" ");

        for (int i = 0; i < msgSplit.length; i++) {
            String urlTitle = "";

            if (isYoutube(msgSplit[i])) {
                /*try {
                    urlTitle = "Youtube";//getYoutubeInfo(msgSplit[i]);
                    sendMessage(channel, sender + "'s YouTube URL: " + urlTitle);
                    break;
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }*/
                break;
            } else if (isUrl(msgSplit[i])) {
                try {
                    urlTitle = getWebpageTitle(msgSplit[i]);
                    sendMessage(channel, sender + "'s URL: " + urlTitle);
                    break;
                } catch (Exception ex1) {
                    ex1.printStackTrace();
                }
            } 
        }

        if (!sender.equalsIgnoreCase(getNick())) {
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

    public void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        if (recipientNick.equalsIgnoreCase(getNick())) {
            joinChannel(channel);
        }
    }

    public void onInvite(String targetNick, String sourceNick, String sourceLogin, String sourceHostname, String channel) {
        if (targetNick.equalsIgnoreCase(getNick())) {
            if (sourceNick.equalsIgnoreCase("ChanServ")) {
                channel = channel.replace("ChanServ!ChanServ@Services.GameSurge.net INVITE Meowzy ", "");
            }
            Log.consoleLog("Invite", "Being invited to " + channel + " by " + sourceNick + " (" + sourceLogin + "@" + sourceHostname + ")");
            if (sourceHostname.equalsIgnoreCase("irc.kieraan.co.uk") || sourceNick.equalsIgnoreCase("ChanServ")) {
                Log.consoleLog("Invite", "Accepting " + sourceNick + "'s invite to " + channel);
                joinChannel(channel);
            }
        }
    }

    public void reConnect() {
        try {
            reconnect();
        } catch (Exception e) {
            try {
                Thread.sleep(10000);
                reConnect();
            } catch (Exception anye) {
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
        for (User user : getUsers(channel)) {
            if (bot.equalsIgnoreCase("%note") && user.getNick().equalsIgnoreCase("Kitteh")) {
                return true;
            }
            if (bot.equalsIgnoreCase("!note") && user.getNick().equalsIgnoreCase("benderj2")) {
                return true;
            }
            if (bot.equalsIgnoreCase(".note")) {
                if (user.getNick().equalsIgnoreCase("Kitteh") || user.getNick().equalsIgnoreCase("benderj2")) {
                    return true;
                }
            }

        }
        return false;
    }

    public static String getWebpageTitle(String s) {
        //Stolen from https://github.com/zack6849/Alphabot/blob/master/src/com/zack6849/alphabot/Utils.java :3
        String title = "";
        String error = "none!";
        try {
            Document doc = Jsoup.connect(s).userAgent("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.56 Safari/537.17").get();

            Elements links = doc.select("title");
            for (Element e1 : links) {
                title += e1.text().replaceAll("\n", "").replaceAll("\\s+", " ");
            }
        } catch (Exception e) {
            error = e.toString().split(":")[1].split("Status=")[1].split(",")[0];
        }
        if (!error.equalsIgnoreCase("none")) {
            if (error.contains("404")) {
                return "Error: 404";
            }
            if (error.contains("502")) {
                return "Error: 502";
            }
            if (error.contains("401")) {
                return "Error: 401";
            }
            if (error.contains("403")) {
                return "Error: 403";
            }
            if (error.contains("500")) {
                return "Error: 500";
            }
            if (error.contains("503")) {
                return "Error: 503";
            }
        }
        return title;
    }

    public static boolean isUrl(String s) {
        String url_regex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern p = Pattern.compile(url_regex);
        Matcher m = p.matcher(s);
        if (m.find()) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isYoutube(String s) {
        String url_regex = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
        Pattern p = Pattern.compile(url_regex, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(s);
        while (m.find()) {
            return true;
        } 
        return false;
    }

    public static String getYoutubeInfo(String s) throws IOException {
        String info;
        String title = null;
        String likes = null;
        String dislikes = null;
        String user = null;
        String veiws = null;
        @SuppressWarnings("unused")
        String publishdate;
        Document doc = Jsoup.connect(s).userAgent("Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.57 Safari/537.17").get();
        for (Element e : doc.select("a")) {
            if (e.attr("class").equalsIgnoreCase("yt-uix-sessionlink yt-user-videos")) {
                user = e.attr("href").split("/user/")[1].split("/")[0];
            }
        }
        for (Element e : doc.select("span")) {
            if (e.attr("class").equalsIgnoreCase("watch-view-count")) {
                veiws = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("likes-count")) {
                likes = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("dislikes-count")) {
                dislikes = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("watch-title  yt-uix-expander-head") || e.attr("class").equalsIgnoreCase("watch-title long-title yt-uix-expander-head")) {
                title = e.text();
            }
            if (e.attr("class").equalsIgnoreCase("watch-video-date")) {
                publishdate = e.text();
            }
        }
        info = title + " - " + user + "  Views: " + veiws + "  Likes: " + likes + "  Dislikes: " + dislikes;
        //System.out.println(info);
        return info;
    }

}
