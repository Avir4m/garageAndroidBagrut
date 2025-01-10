package com.example.garage.functions;

public class passwordValidation {

    public static String isPasswordValid(String password) {
        if (password.length() < 7) {
            return "Password is shorter than 7 characters.";
        }

        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (int i = 0; i < password.length(); i++) {
            char ch = password.charAt(i);

            if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(ch)) {
                hasUpperCase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecialChar = true;
            }

            if (hasLowerCase && hasUpperCase && hasDigit && hasSpecialChar) {
                break;
            }
        }

        if (!hasLowerCase) {
            return "Password must contain at least one lowercase letter.";
        }
        if (!hasUpperCase) {
            return "Password must contain at least one uppercase letter.";
        }
        if (!hasDigit) {
            return "Password must contain at least one digit.";
        }
        if (!hasSpecialChar) {
            return "Password must contain at least one special character.";
        }

        return null;
    }
}
