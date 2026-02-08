package src;
import java.io.Console;
public class Utils {
    public static char[] readPassword(String prompt) {
        Console console = System.console();
        if (console == null) {
            throw new RuntimeException(
                "No console available. Run Encryp70r from a terminal."
            );
        }
        return console.readPassword(prompt);
    }
}