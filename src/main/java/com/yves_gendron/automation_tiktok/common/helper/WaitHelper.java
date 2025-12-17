package com.yves_gendron.automation_tiktok.common.helper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WaitHelper {

    public static void waitRandomlyInRange(long from, long to) {
        try {
            Thread.sleep((long) (from + Math.random() * (to - from)));
        } catch (InterruptedException e) {
            log.error("Error waiting ", e);
            Thread.currentThread().interrupt();
        }
    }

    public static void waitSafely(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            log.error("Error waiting ", e);
            Thread.currentThread().interrupt();
        }
    }
}
