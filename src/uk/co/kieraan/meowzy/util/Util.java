package uk.co.kieraan.meowzy.util;

public class Util {

    public static String combineSplit(int startIndex, String[] string, String seperator) {
        final StringBuilder builder = new StringBuilder();
        for (int i = startIndex; i < string.length; i++) {
            builder.append(string[i]);
            builder.append(seperator);
        }
        builder.deleteCharAt(builder.length() - seperator.length());
        return builder.toString();
    }

}
