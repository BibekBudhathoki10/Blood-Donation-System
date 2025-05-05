package util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for password hashing and verification using PBKDF2.
 */
public class PasswordUtil {
    
    // Algorithm parameters
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    
    // Delimiter for storing salt and hash together
    private static final String DELIMITER = ":";
    
    /**
     * Hash a password using PBKDF2.
     * 
     * @param plainTextPassword The password to hash
     * @return The hashed password with salt (format: "salt:hash")
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password
            byte[] hash = pbkdf2(plainTextPassword.toCharArray(), salt);
            
            // Combine salt and hash
            return Base64.getEncoder().encodeToString(salt) + DELIMITER + 
                   Base64.getEncoder().encodeToString(hash);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a stored hash.
     * 
     * @param plainTextPassword The password to check
     * @param storedPassword The stored password hash (format: "salt:hash")
     * @return True if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String plainTextPassword, String storedPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty() || 
            storedPassword == null || storedPassword.isEmpty()) {
            return false;
        }
        
        try {
            // Split the stored password into salt and hash
            String[] parts = storedPassword.split(DELIMITER);
            if (parts.length != 2) {
                return false;
            }
            
            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);
            
            // Hash the input password with the same salt
            byte[] testHash = pbkdf2(plainTextPassword.toCharArray(), salt);
            
            // Compare the hashes
            return Arrays.equals(hash, testHash);
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * Applies PBKDF2 to the password.
     * 
     * @param password The password to hash
     * @param salt The salt to use
     * @return The hashed password
     * @throws NoSuchAlgorithmException If the algorithm is not available
     * @throws InvalidKeySpecException If the key spec is invalid
     */
    private static byte[] pbkdf2(char[] password, byte[] salt) 
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        
        KeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        return factory.generateSecret(spec).getEncoded();
    }
}
