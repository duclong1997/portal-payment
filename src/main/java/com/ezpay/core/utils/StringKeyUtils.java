package com.ezpay.core.utils;


import javax.servlet.http.HttpServletRequest;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

/**
 * @author OI
 */
public class StringKeyUtils {
    public static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static final String LOWER = UPPER.toLowerCase(Locale.ROOT);

    public static final String DIGITS = "0123456789";

    public static final String ALPHANUMERIC = UPPER + LOWER + DIGITS;


    public static String generatedUid() {
        return UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    public static String generatedKey() {
        return randomString(24);
    }

    public static String randomString(int length) {
        char[] buf = new char[length];
        Random random = new SecureRandom();
        for (int idx = 0; idx < length; ++idx) {
            buf[idx] = ALPHANUMERIC.charAt(random.nextInt(ALPHANUMERIC.length()));
        }
        return new String(buf);
    }


    public static String randomNumber(int length) {
        char[] buf = new char[length];
        Random random = new SecureRandom();
        for (int idx = 0; idx < length; ++idx) {
            buf[idx] = DIGITS.charAt(random.nextInt(DIGITS.length()));
        }
        return new String(buf);
    }


    public static String generatedtxnRef() {
        return generatedUid().substring(16) + System.currentTimeMillis();
    }

    public static String generatedtxnRef2() {
        return randomNumber(2) + System.currentTimeMillis();
    }

    public static String getIp(HttpServletRequest httpReq) {
        String ipAddress = httpReq.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = httpReq.getRemoteAddr();
        }
        return ipAddress;
    }
}