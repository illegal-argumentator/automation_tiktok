package com.yves_gendron.automation_tiktok.common.utils;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import lombok.extern.slf4j.Slf4j;


import java.util.Random;

import static com.yves_gendron.automation_tiktok.common.utils.ThreadUtils.sleep;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.PASSWORD_INPUT;

@Slf4j
public class PlayWrightUtils {
    private static final Random RANDOM = new Random();

    public static void fill(Page page, String selector, String text) {
        Locator locator = page.locator(selector);
        fill(locator, text);
    }

    public static void fill(Locator locator, String text) {
        locator.pressSequentially(text, new Locator.PressSequentiallyOptions().setDelay(RANDOM.nextInt(30, 70)));
    }

    public static void executeClick(Page page, String selector) {
        Locator btn = page.locator(selector);
        executeClick(btn, page);
    }


    public static void executeClick(Locator btn, Page page)  {


        scrollIntoView(btn);
        String target = (String) btn.evaluate("el => el.target");
        if ("_blank".equals(target)) {
            btn.evaluate("el => el.removeAttribute('target')");
        }

        setHover(btn, page);

        try {
            // Get element bounding box for real mouse movement
            var boundingBox = btn.boundingBox();
            if (boundingBox == null) {
                btn.click();
                sleep(500, 500);
                return;
            }

            // Calculate target position with slight random offset (humans don't click exactly in center)
            double offsetX = (RANDOM.nextDouble() - 0.5) * boundingBox.width * 0.3; // ±30% от центра
            double offsetY = (RANDOM.nextDouble() - 0.5) * boundingBox.height * 0.3;
            double targetX = boundingBox.x + boundingBox.width / 2 + offsetX;
            double targetY = boundingBox.y + boundingBox.height / 2 + offsetY;

            // Get current mouse position (assume starting from random position)
            double startX = RANDOM.nextDouble() * 200 + 50;
            double startY = RANDOM.nextDouble() * 200 + 50;

            // Create smooth movement with multiple steps and bezier curve
            int steps = 12 + RANDOM.nextInt(8); // 12-19 steps for more natural movement

            // Control points for bezier curve (creates more natural arc)
            double ctrlX1 = startX + (targetX - startX) * (0.3 + RANDOM.nextDouble() * 0.2);
            double ctrlY1 = startY + (targetY - startY) * (0.2 + RANDOM.nextDouble() * 0.3);
            double ctrlX2 = startX + (targetX - startX) * (0.6 + RANDOM.nextDouble() * 0.2);
            double ctrlY2 = startY + (targetY - startY) * (0.7 + RANDOM.nextDouble() * 0.2);

            for (int i = 1; i <= steps; i++) {
                double t = (double) i / steps;

                // Cubic bezier curve for more natural movement
                double u = 1 - t;
                double tt = t * t;
                double uu = u * u;
                double uuu = uu * u;
                double ttt = tt * t;

                double currentX = uuu * startX +
                        3 * uu * t * ctrlX1 +
                        3 * u * tt * ctrlX2 +
                        ttt * targetX;
                double currentY = uuu * startY +
                        3 * uu * t * ctrlY1 +
                        3 * u * tt * ctrlY2 +
                        ttt * targetY;

                // Add slight jitter (human hand tremor)
                currentX += (RANDOM.nextDouble() - 0.5) * 2;
                currentY += (RANDOM.nextDouble() - 0.5) * 2;

                page.mouse().move(currentX, currentY);

                // Variable speed - slower at start and end, faster in middle
                if (i < 3 || i > steps - 3) {
                    sleep(25, 15); // 25-40ms
                } else {
                    sleep(12, 8); // 12-20ms
                }
            }


            sleep(15, 25); // 25-40ms
            // Press mouse button down with human-like delay
            page.mouse().down();
            sleep(80, 90); // 80-170ms hold (humans vary a lot)

            // Release mouse button
            page.mouse().up();
            sleep(300, 400); // 300-700ms after click
        } catch (Exception e) {
            // Last resort fallback
            log.debug("Advanced click failed, using fallback", e);
            try {
                btn.click();
                sleep(500, 500);
            } catch (Exception ex) {
                btn.evaluate("el => el.dispatchEvent(new MouseEvent('click', {bubbles: true, cancelable: true}))");
            }
        }
    }

    private static void setHover(Locator element, Page page) {
        try {
            // Scroll element into view first
            element.evaluate("el => el.scrollIntoView({behavior: 'auto', block: 'center', inline: 'center'})");
            sleep(150, 100);

            // Get element bounding box
            var boundingBox = element.boundingBox();
            if (boundingBox == null) {
                return;
            }
            // Calculate target with slight offset
            double offsetX = (RANDOM.nextDouble() - 0.5) * boundingBox.width * 0.2;
            double offsetY = (RANDOM.nextDouble() - 0.5) * boundingBox.height * 0.2;
            double targetX = boundingBox.x + boundingBox.width / 2 + offsetX;
            double targetY = boundingBox.y + boundingBox.height / 2 + offsetY;

            // Get current mouse position (random start)
            double startX = RANDOM.nextDouble() * 150 + 50;
            double startY = RANDOM.nextDouble() * 150 + 50;

            // Create smooth bezier curve movement
            int steps = 10 + RANDOM.nextInt(6); // 10-15 steps

            // Control points for bezier curve
            double ctrlX1 = startX + (targetX - startX) * (0.25 + RANDOM.nextDouble() * 0.2);
            double ctrlY1 = startY + (targetY - startY) * (0.2 + RANDOM.nextDouble() * 0.25);
            double ctrlX2 = startX + (targetX - startX) * (0.65 + RANDOM.nextDouble() * 0.15);
            double ctrlY2 = startY + (targetY - startY) * (0.75 + RANDOM.nextDouble() * 0.15);

            for (int i = 1; i <= steps; i++) {
                double t = (double) i / steps;
                double u = 1 - t;
                double tt = t * t;
                double uu = u * u;

                // Cubic bezier
                double currentX = uu * u * startX +
                        3 * uu * t * ctrlX1 +
                        3 * u * tt * ctrlX2 +
                        tt * t * targetX;
                double currentY = uu * u * startY +
                        3 * uu * t * ctrlY1 +
                        3 * u * tt * ctrlY2 +
                        tt * t * targetY;

                // Add jitter
                currentX += (RANDOM.nextDouble() - 0.5) * 1.5;
                currentY += (RANDOM.nextDouble() - 0.5) * 1.5;

                page.mouse().move(currentX, currentY);
                sleep(18, 12); // 18-30ms
            }

            sleep(150, 150); // 150-300ms pause after reaching target
        } catch (Exception e) {
            log.debug("Failed to set hover", e);
        }
    }


    private static void scrollIntoView(Locator btn) {
        try {
            btn.evaluate(
                    "el => { if (el && typeof el.scrollIntoViewIfNeeded === 'function') el.scrollIntoViewIfNeeded(); else if (el) el.scrollIntoView({behavior: 'smooth', block: 'center'}); }"
            );
            sleep(500);
        } catch (Exception e) {
            log.debug("Failed to scroll into view", e);
        }
    }

}
