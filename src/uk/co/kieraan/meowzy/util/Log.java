package uk.co.kieraan.meowzy.util;

import java.util.Date;

public class Log {
    
    public static void consoleLog(String type, String logMessage) {
        type = type.toUpperCase();
        String time = new Date().toString();
        System.out.println(time + " [" + type + "] " + logMessage);
    }

    public static void consoleLog(String logMessage) {
        String time = new Date().toString();
        System.out.println(time + " [INFO] " + logMessage);
    }

}
