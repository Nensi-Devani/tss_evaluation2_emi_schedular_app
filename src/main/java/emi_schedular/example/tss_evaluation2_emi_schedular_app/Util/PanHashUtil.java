package emi_schedular.example.tss_evaluation2_emi_schedular_app.Util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PanHashUtil {

    public static String hashPan(String pan) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(pan.getBytes());

            StringBuilder hexString = new StringBuilder();

            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing PAN", e);
        }
    }

}
