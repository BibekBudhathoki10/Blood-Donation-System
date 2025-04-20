package util;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    // Email validation
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    
    // Phone validation
    private static final String PHONE_REGEX = "^\\d{10}$";
    private static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    
    // Password validation (at least 8 characters, containing letters and numbers)
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    
    // Blood group validation
    private static final String BLOOD_GROUP_REGEX = "^(A|B|AB|O)[+-]$";
    private static final Pattern BLOOD_GROUP_PATTERN = Pattern.compile(BLOOD_GROUP_REGEX);
    
    // Validate email
    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    // Validate phone
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    // Validate password
    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    // Validate blood group
    public static boolean isValidBloodGroup(String bloodGroup) {
        if (bloodGroup == null) {
            return false;
        }
        return BLOOD_GROUP_PATTERN.matcher(bloodGroup).matches();
    }
    
    // Validate required field
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    // Validate numeric value
    public static boolean isNumeric(String value) {
        if (value == null) {
            return false;
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    // Validate positive numeric value
    public static boolean isPositiveNumeric(String value) {
        if (!isNumeric(value)) {
            return false;
        }
        return Integer.parseInt(value) > 0;
    }
}
