package src;
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

            String msg = e.getMessage();

            if (msg != null && (msg.contains("AEADBadTagException") ||
                    msg.contains("Tag mismatch") ||
                    msg.toLowerCase().contains("wrong password"))) {
                System.err.println("Decryption failed. Wrong password or file has been tampered.");
            } else if (msg != null) {
                System.err.println(msg);
            } else {
                System.err.println("Operation failed.");
            }
        }

    }

    private static void handleEncrypt(Path inputFile) throws Exception {
        char[] password = Utils.readPassword("Enter password: ");
        char[] confirm = Utils.readPassword("Confirm password: ");

        if (!Arrays.equals(password, confirm)) {
            throw new RuntimeException("Passwords do not match.");
        }
        Path output = inputFile.resolveSibling(
                inputFile.getFileName().toString() + ".enc");

        if (Files.exists(output)) {
            System.out.println("Encrypted file already exists: " + output);
            return;
        }

        try {
            CryptoEngine.encrypt(inputFile, output, password);
            System.out.println("File encrypted successfully → " + output);
        } catch (Exception e) {
            System.out.println("Encryption failed: " + e.getMessage());
        } finally {
            java.util.Arrays.fill(password, '\0'); 
        }

    }

    private static void handleDecrypt(Path encryptedFile) throws Exception {
        char[] password = Utils.readPassword("Enter password: ");
        String name = encryptedFile.getFileName().toString();

        if (!name.endsWith(".enc")) {
            System.out.println("Invalid file. Not a .enc encrypted file.");
            return;
        }

        String originalName = name.substring(0, name.length() - 4);

        Path output = encryptedFile.resolveSibling(originalName);

        if (Files.exists(output)) {
            System.out.println("Decrypted file already exists: " + output);
            return;
        }

        try {
            CryptoEngine.decrypt(encryptedFile, output, password);
            System.out.println("File decrypted successfully → " + output);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            java.util.Arrays.fill(password, '\0'); 
        }

    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  java Encryp70r encrypt <file>");
        System.out.println("  java Encryp70r decrypt <file.enc>");
    }
}
