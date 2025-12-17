package com.yves_gendron.automation_tiktok.system.service.captcha.tiktokcaptcha;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Mouse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.BoundingBox;
import com.yves_gendron.automation_tiktok.system.client.sadcaptcha.SadCaptchaClient;
import com.yves_gendron.automation_tiktok.system.client.sadcaptcha.common.dto.RotateCaptchaResponse;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.captcha.CaptchaSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitRandomlyInRange;
import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitSafely;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokCaptchaSolver implements CaptchaSolver {

    private final SadCaptchaClient sadCaptchaClient;

    private final PlaywrightWaiter playwrightWaiter;

    @Override
    public void solve(Page page) {
        log.info("Solving rotation captcha");

        for (int attempt = 1; attempt <= 3; attempt++) {
            if (hasSolved(page)) {
                log.info("Successfully solved captcha");
                break;
            }

            log.info("Attempting to solve the captcha: " + attempt + "/3");

            try {
                List<String> base64List = getCaptchaBase64Images(page);
                if (base64List.size() < 2 || base64List.get(0) == null || base64List.get(1) == null) {
                    log.warn("Captcha images not loaded, retrying...");
                    waitSafely(1000);
                    continue;
                }

                double angle = solveCaptcha(base64List.get(0), base64List.get(1));
                moveSlider(page, angle);

            } catch (PlaywrightException e) {
                log.warn("Captcha solving failed this attempt: " + e.getMessage());
            }
        }
    }

    private List<String> getCaptchaBase64Images(Page page) {
        page.waitForSelector(CAPTCHA_IMG, new Page.WaitForSelectorOptions().setTimeout(15000));

        @SuppressWarnings("unchecked")
        List<String> base64List = (List<String>) page.evaluate("() => {" +
                "const imgs = Array.from(document.querySelectorAll('img[alt=\"Captcha\"]'));" +
                "return imgs.map(img => {" +
                "   if (!img || !img.complete || img.naturalWidth === 0) return null;" +
                "   const canvas = document.createElement('canvas');" +
                "   canvas.width = img.naturalWidth;" +
                "   canvas.height = img.naturalHeight;" +
                "   const ctx = canvas.getContext('2d');" +
                "   ctx.drawImage(img, 0, 0);" +
                "   return canvas.toDataURL('image/png').split(',')[1];" +
                "});" +
                "}");
        return base64List;
    }

    private double solveCaptcha(String outerBase64, String innerBase64) {
        RotateCaptchaResponse response = sadCaptchaClient.rotate(outerBase64, innerBase64);
        return response.getAngle();
    }

    private void moveSlider(Page page, double angle) {
        Locator slidebar = page.locator(CAPTCHA_SLIDEBAR_CLASS);
        Locator sliderIcon = page.locator(CAPTCHA_SLIDER_ICON_CLASS);

        BoundingBox slideBox = slidebar.boundingBox();
        BoundingBox iconBox = sliderIcon.boundingBox();

        double maxDistance = slideBox.width - iconBox.width;
        double d = (maxDistance * angle) / 360.0;

        double noise = (Math.random() - 0.5) * 4;
        double targetX = iconBox.x + iconBox.width / 2 + d + noise;
        double startX = iconBox.x + iconBox.width / 2;
        double startY = iconBox.y + iconBox.height / 2;

        page.mouse().move(startX, startY);
        waitRandomlyInRange(200, 400);
        page.mouse().down();

        int steps = 10;
        for (int j = 1; j <= steps; j++) {
            double x = startX + ((targetX - startX) * j / steps) + (Math.random() - 0.5) * 2;
            page.mouse().move(x, startY, new Mouse.MoveOptions().setSteps(1));
            waitRandomlyInRange(20, 50);
        }

        waitRandomlyInRange(100, 200);
        page.mouse().up();
        waitRandomlyInRange(300, 500);
    }

    private boolean hasSolved(Page page) {
        Locator captchaLocator = page.locator(CAPTCHA_IMG).first();
        playwrightWaiter.waitForSelector(4000, captchaLocator);
        return !captchaLocator.isVisible();
    }
}
