/**
 * Created by linoor on 11/17/15.
 */
public class ColorPrint {
    public static final String[] colors = {
            "\u001B[30m",
            "\u001B[31m",
            "\u001B[32m",
            "\u001B[33m",
            "\u001B[34;1m",
            "\u001B[35;1m",
            "\u001B[36;1m",
            "\u001B[37;1m"
    };

    public final static String ANSI_RESET = "\u001B[0m";

    public static String getColoredString(int colorNum, String s) {
        return colors[colorNum % colors.length] + s + ANSI_RESET;
    }
}
