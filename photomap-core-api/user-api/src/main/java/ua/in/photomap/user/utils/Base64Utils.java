package ua.in.photomap.user.utils;

import lombok.experimental.UtilityClass;

import java.util.Base64;

@UtilityClass
public class Base64Utils {
    public static String decode(String base64) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        return new String(decodedBytes);
    }
}