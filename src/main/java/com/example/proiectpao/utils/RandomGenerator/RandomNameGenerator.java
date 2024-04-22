package com.example.proiectpao.utils.RandomGenerator;

import java.util.Random;

public class RandomNameGenerator {
    private static RandomNameGenerator instance = null;
    private static String chars;
    private static int len;

    private RandomNameGenerator() {
        chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        len = 10;
    }

    public static RandomNameGenerator getInstance() {
        if (instance == null) {
            instance = new RandomNameGenerator();
        }
        return instance;
    }

    public String generateName() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            int randomIndex = random.nextInt(chars.length());
            sb.append(chars.charAt(randomIndex));
        }
        return sb.toString();
    }
}
