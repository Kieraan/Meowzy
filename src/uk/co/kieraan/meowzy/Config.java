package uk.co.kieraan.meowzy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import uk.co.kieraan.meowzy.util.Log;

public class Config {

    Meowzy bot;

    public Config(Meowzy bot) {
        this.bot = bot;
        loadConfiguration();
    }

    // Config file
    private static String configurationFile = "config.properties";

    // Bot
    private static String botNickname;
    private static String botAuthMethod;
    private static String botAuthPassword;
    private static String botLogin;
    private static String botVersion;
    private static String botFinger;
    private static int botMessageDelay;
    private static String[] botChannels;
    private static boolean botAutoNickChange;
    private static boolean botWelcomeUsers;
    private static String botWelcomeMessage;
    private static String botCommandPrefix;

    // Users
    private static String[] usersAccessList;
    private static boolean usersOpAddAlias;
    private static boolean usersVoiceAddAlias;
    // Server
    private static String serverAddress;
    private static String serverPassword;
    private static int serverPort;

    // Dev
    private static boolean devVerboseOutput;
    private static boolean devStateVersionOnJoin;

    public static void saveNewConfiguration() {
        // TODO: Generate config
    }

    public static void loadConfiguration() {
        Properties configuration = new Properties();
        Log.consoleLog("Loading configuration file.");
        try {
            configuration.load(new FileInputStream(configurationFile));
        } catch (IOException ex) {
            Log.consoleLog("Error", "Cannot load configuration file.");
            // TODO: meowzy.shutDown();
            ex.printStackTrace();
            return;
        }
        Log.consoleLog("Configuration file loaded.");

        // Bot
        botNickname = configuration.getProperty("Nickname");
        botAuthMethod = configuration.getProperty("Auth_Method");
        botAuthPassword = configuration.getProperty("Auth_Password");
        botLogin = configuration.getProperty("Login");
        botVersion = configuration.getProperty("Version");
        botFinger = configuration.getProperty("Finger");
        botMessageDelay = Integer.parseInt(configuration.getProperty("Message_Delay"));
        botChannels = configuration.getProperty("Channels").split("@");
        botAutoNickChange = Boolean.parseBoolean(configuration.getProperty("Auto_Nick_Change"));
        botWelcomeUsers = Boolean.parseBoolean(configuration.getProperty("Welcome_Users"));
        botWelcomeMessage = configuration.getProperty("Welcome_Message");
        botCommandPrefix = configuration.getProperty("Command_Prefix");

        // Users
        usersAccessList = configuration.getProperty("Access_List").split("@");
        usersOpAddAlias = Boolean.parseBoolean(configuration.getProperty("Op_Can_Add_Alias"));
        usersVoiceAddAlias = Boolean.parseBoolean(configuration.getProperty("Voice_Can_Add_Alias"));
        // Server
        serverAddress = configuration.getProperty("Server_Address");
        serverPassword = configuration.getProperty("Server_Password");
        serverPort = Integer.parseInt(configuration.getProperty("Server_Port"));

        // Dev
        devVerboseOutput = Boolean.parseBoolean(configuration.getProperty("Output_Verbose"));
        devStateVersionOnJoin = Boolean.parseBoolean(configuration.getProperty("State_Version_On_Join"));
    }

    public static String getNick() {
        return botNickname;
    }

    public static String getAuthMethod() {
        return botAuthMethod;
    }

    public static String getAuthPassword() {
        return botAuthPassword;
    }

    public static String getLogin() {
        return botLogin;
    }

    public static String getVersion() {
        return botVersion;
    }

    public static String getFinger() {
        return botFinger;
    }

    public static int getMessageDelay() {
        return botMessageDelay;
    }

    public static String[] getChannels() {
        return botChannels;
    }

    public static boolean getAutoNickChange() {
        return botAutoNickChange;
    }

    public static boolean getWelcomeUsers() {
        return botWelcomeUsers;
    }

    public static String getWelcomeMessage() {
        return botWelcomeMessage;
    }

    public static String getCommandPrefix() {
        return botCommandPrefix;
    }

    public static String getServerAddress() {
        return serverAddress;
    }

    public static String getServerPassword() {
        return serverPassword;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String[] getAccessList() {
        return usersAccessList;
    }

    public static boolean getOpCanAddAlias() {
        return usersOpAddAlias;
    }

    public static boolean getVoiceCanAddAlias() {
        return usersVoiceAddAlias;
    }

    public static boolean getVersboseOutput() {
        return devVerboseOutput;
    }

    public static boolean getStateVersionOnJoin() {
        return devStateVersionOnJoin;
    }
}
