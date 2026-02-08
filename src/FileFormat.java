package src;
public class FileFormat {
    public static final byte[] MAGIC = new byte[] { 'E', '7', '0', 'R' };
    public static final int SALT_SIZE = 16;
    public static final int IV_SIZE = 12;
}