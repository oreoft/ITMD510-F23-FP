package cn.someget.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * encode password
 *
 * @author zyf
 * @date 2022-08-13 14:08
 */
public class Md5Utils {
    public static String toMD5(String plainText) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(plainText.getBytes());
        byte[] byteData = md.digest();
        // convert the byte to hex format method 1
        StringBuilder sbHash = new StringBuilder();
        for (byte byteDatum : byteData) {
            sbHash.append(Integer.toString((byteDatum & 0xff) + 0x100, 16).substring(1));
        }
        return sbHash.toString();
    }
}