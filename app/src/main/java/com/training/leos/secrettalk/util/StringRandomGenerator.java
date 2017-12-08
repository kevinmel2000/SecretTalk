package com.training.leos.secrettalk.util;

import java.util.Random;

/**
 * Created by Leo on 08/12/2017.
 */

public class StringRandomGenerator {
    public static String randomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPRSTUVWXYZ1234567890" +
                "abcdefghijklmnopqrstuvwxyz";
        Random rand = new Random();
        StringBuilder buf = new StringBuilder();
        for (int i=0; i<length; i++) {
            buf.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return buf.toString();
    }
}
