package src;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;

public class CryptoEngine {

    private static final int ITERATIONS = 100_000;
    private static final int KEY_SIZE = 256; 

    private static SecretKey deriveKey(char[] password, byte[] salt) throws Exception {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_SIZE);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();

        Arrays.fill(password, '\0');

        return new SecretKeySpec(keyBytes, "AES");
    }


    public static void encrypt(Path inputFile, Path outputFile, char[] password)
            throws Exception {

        SecureRandom random = new SecureRandom();

        byte[] salt = new byte[FileFormat.SALT_SIZE];
        byte[] iv = new byte[FileFormat.IV_SIZE];

        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        try (
                FileInputStream fis = new FileInputStream(inputFile.toFile());
                FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {
            fos.write(FileFormat.MAGIC);
            fos.write(salt);
            fos.write(iv);

            try (CipherOutputStream cos = new CipherOutputStream(fos, cipher)) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    cos.write(buffer, 0, bytesRead);
                }
            }
        }
    }


    public static void decrypt(Path encryptedFile, Path outputFile, char[] password)
            throws Exception {

        try (FileInputStream fis = new FileInputStream(encryptedFile.toFile())) {

            byte[] magic = new byte[FileFormat.MAGIC.length];
            if (fis.read(magic) != magic.length ||
                    !Arrays.equals(magic, FileFormat.MAGIC)) {
                throw new RuntimeException("Invalid or unsupported file format.");
            }

            byte[] salt = fis.readNBytes(FileFormat.SALT_SIZE);
            byte[] iv = fis.readNBytes(FileFormat.IV_SIZE);

            if (salt.length != FileFormat.SALT_SIZE ||
                    iv.length != FileFormat.IV_SIZE) {
                throw new RuntimeException("Corrupted or incomplete encrypted file.");
            }

            SecretKey key = deriveKey(password, salt);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            try (
                    CipherInputStream cis = new CipherInputStream(fis, cipher);
                    FileOutputStream fos = new FileOutputStream(outputFile.toFile())) {
                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = cis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(
                    "Decryption failed. Wrong password or file has been tampered.");
        }

    }
}
