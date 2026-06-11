import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtils {

    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(plainPassword.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed: " + e.getMessage());
        }
    }

    public static boolean checkPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }
}