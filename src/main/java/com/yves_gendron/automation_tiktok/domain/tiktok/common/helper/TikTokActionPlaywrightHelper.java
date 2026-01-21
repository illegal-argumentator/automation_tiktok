package com.yves_gendron.automation_tiktok.domain.tiktok.common.helper;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.FilePayload;
import com.yves_gendron.automation_tiktok.common.utils.PlayWrightUtils;
import com.yves_gendron.automation_tiktok.common.utils.tries.TryUtils;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokActionException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokBlockActivityException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCaptchaException;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import com.yves_gendron.automation_tiktok.system.service.captcha.tiktokcaptcha.TikTokCaptchaSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitRandomlyInRange;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokActionPlaywrightHelper {

    private final PlaywrightWaiter playwrightWaiter;

    private final TikTokCaptchaSolver tikTokCaptchaSolver;

    public void processBrowserLogIn(Page page, TikTokAccount tikTokAccount) {
        TryUtils.tryRun(() -> playwrightWaiter.waitForSelector(40_000, page.locator(CONTINUE_WITH_FACEBOOK_DIV)), 2, () -> page.reload());
        PlayWrightUtils.executeClick(page,LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME_TEXT);
//        page.click(LOG_IN_USE_PHONE_OR_EMAIL_OR_USERNAME_TEXT);

        page.waitForSelector(LOG_IN_WITH_EMAIL_OR_USERNAME_TEXT);
        waitRandomlyInRange(1200, 1600);
//        page.click(LOG_IN_WITH_EMAIL_OR_USERNAME_TEXT);
        PlayWrightUtils.executeClick(page,LOG_IN_WITH_EMAIL_OR_USERNAME_TEXT);

        page.waitForSelector(LOG_IN_EMAIL_INPUT);
        waitRandomlyInRange(1200, 1600);
        PlayWrightUtils.fill(page,LOG_IN_EMAIL_INPUT, tikTokAccount.getEmail());
//        page.fill(LOG_IN_EMAIL_INPUT, tikTokAccount.getEmail());

        waitRandomlyInRange(1200, 1600);
//        page.fill(PASSWORD_INPUT, tikTokAccount.getPassword());
        PlayWrightUtils.fill(page,PASSWORD_INPUT, tikTokAccount.getPassword());

        waitRandomlyInRange(1200, 1600);
        page.click(LOG_IN_BUTTON);
        PlayWrightUtils.executeClick(page,LOG_IN_BUTTON);
        waitRandomlyInRange(1200, 1600);

        handleAfterLogInBlockers(page, tikTokAccount);
    }

    public void processAvatarSetting(TikTokAccount tikTokAccount, byte[] avatarBinaries, PlaywrightDto playwrightDto) {
        try {
            log.info("Started adding avatar");
            Page page = playwrightDto.getPage();

            navigateToAvatarChange(page);

            waitRandomlyInRange(1100, 1300);
            Locator profileAvatarInput = page.locator(FILE_INPUT);
            profileAvatarInput.setInputFiles(new FilePayload("avatar", "image/jpeg", avatarBinaries));

            saveAvatarAndWaitForUpload(page);

            log.info("Avatar successfully uploaded");
        } catch (Exception e) {
            log.error("Couldn't upload avatar ", e);
            throw new TikTokActionException(tikTokAccount, "Couldn't upload avatar");
        }
    }

    public boolean isVideoLive(Page page, int videoIndex) {
        Locator liveNowIcon = page.locator(selectLiveNow(videoIndex));
        return playwrightWaiter.waitForSelector(1000, liveNowIcon);
    }

    public boolean isCommentsDisabled(Page page) {
        Locator commentsDisabledLocator = page.locator(COMMENTS_TURNED_OFF_TEXT);
        return playwrightWaiter.waitForSelector(1000, commentsDisabledLocator);
    }

    private void navigateToAvatarChange(Page page) {
        waitRandomlyInRange(1000, 1200);
        playwrightWaiter.waitForSelector(60_000, page.locator(PROFILE_BUTTON));
        page.click(PROFILE_BUTTON);

        waitRandomlyInRange(800, 1500);
        page.waitForSelector(EDIT_PROFILE_BUTTON);
        page.click(EDIT_PROFILE_BUTTON);
    }

    private void saveAvatarAndWaitForUpload(Page page) {
        waitRandomlyInRange(1000, 1500);
        page.waitForSelector(APPLY_BUTTON);
        page.click(APPLY_BUTTON);

        waitRandomlyInRange(1000, 1300);
        page.waitForSelector(SAVE_BUTTON);
        page.click(SAVE_BUTTON);

        page.waitForSelector(PROFILE_HAS_BEED_UPDATED_SPAN);
    }

    private void handleAfterLogInBlockers(Page page, TikTokAccount tikTokAccount) {
        handleTikTokCaptcha(page, tikTokAccount);
        handleSuspiciousActivity(page, tikTokAccount);
    }

    private void handleTikTokCaptcha(Page page, TikTokAccount tikTokAccount) {
        List<String> selectorsToAppearAfterSignIn = List.of(LOGGED_IN_TEXT, CAPTCHA_ID);
        playwrightWaiter.waitForSelectorAndAct(15_000, page.locator(String.join(", ", selectorsToAppearAfterSignIn)), locator -> {
            if (!page.locator(CAPTCHA_ID).isVisible()){
                log.info("Successfully logged in");
                return;
            }
            try {
                log.warn("Captcha appeared on log in. Solving...");
                TryUtils.tryRun(() -> playwrightWaiter.waitForSelector(30_000, page.locator(CAPTCHA_IMG)));
//                    tikTokCaptchaSolver.solve(page);
            } catch (Exception e) {
                log.error("Captcha not solved: ", e);
                throw new TikTokCaptchaException(tikTokAccount, "Tik tok rotation captcha not solved");
            }
        });
    }

    private void handleSuspiciousActivity(Page page, TikTokAccount tikTokAccount) {
        List<String> suspiciousActivitySelectors = List.of(
                MAXIMUM_ATTEMPTS_REACHED_TEXT,
                PLEASE_TRY_AGAIN_SPAN,
                SUSPICIOUS_ACTIVITY_DETECTED_H1
        );
        for (String selector : suspiciousActivitySelectors) {
            Locator locator = page.locator(selector);
            playwrightWaiter.waitForSelectorAndAct(6000, locator, l -> {
                if (l.isVisible()) {
                    throw new TikTokBlockActivityException(
                            tikTokAccount,
                            "Could not log in. TikTok detected suspicious activity"
                    );
                }
            });
        }
    }

}
