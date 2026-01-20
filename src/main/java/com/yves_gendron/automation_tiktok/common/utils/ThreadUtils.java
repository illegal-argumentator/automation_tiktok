package com.yves_gendron.automation_tiktok.common.utils;

import java.util.Random;

public class ThreadUtils {
    private static final Random RANDOM = new Random();
    private ThreadUtils() {
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long millis, long additionMs ) {
        try {
            Thread.sleep(millis + RANDOM.nextLong(additionMs));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Runnable sleepRunnable(long millis) {
        return () -> sleep(millis);
    }

    public static Runnable sleepRunnable(long millis, long additionMs) {
        return () -> sleep(millis,additionMs);
    }
}
