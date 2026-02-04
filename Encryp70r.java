import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
public class Encryp70r {
    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
            return;
        }
        String mode = args[0];
        Path filePath = Path.of(args[1]);
        if (!Files.exists(filePath)) {
            System.err.println("File does not exist.");
            return;
        }
        try {
            if (mode.equalsIgnoreCase("encrypt")) {
                handleEncrypt(filePath);
            } else if (mode.equalsIgnoreCase("decrypt")) {
                handleDecrypt(filePath);
            } else {
                printUsage();
            }
        } catch (Exception e) {
            System.err.println("Operation failed: " + e.getMessage());
        }
    }
    private static void handleEncrypt(Path inputFile) throws Exception {
        char[] password = Utils.readPassword("Enter password: ");
        char[] confirm  = Utils.readPassword("Confirm password: ");

        if (!Arrays.equals(password, confirm)) {
            throw new RuntimeException("Passwords do not match.");
        }
        // Encrypt Filler
        System.out.println("Encryption pipeline ready.");
    }
    private static void handleDecrypt(Path encryptedFile) throws Exception {
        char[] password = Utils.readPassword("Enter password: ");
        // Decrypt Filler
        System.out.println("Decryption pipeline ready.");
    }
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java Encryp70r encrypt <file>");
        System.out.println("  java Encryp70r decrypt <file.enc>");
    }
}
