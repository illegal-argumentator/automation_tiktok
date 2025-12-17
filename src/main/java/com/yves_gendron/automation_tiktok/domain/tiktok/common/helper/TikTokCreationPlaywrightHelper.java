package com.yves_gendron.automation_tiktok.domain.tiktok.common.helper;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokBlockActivityException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCaptchaException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCreationException;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.client.mailtm.MailTmService;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import com.yves_gendron.automation_tiktok.system.service.captcha.tiktokcaptcha.TikTokCaptchaSolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitRandomlyInRange;
import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitSafely;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokConstants.TIKTOK_SIGN_UP_DUCK_DUCK_GO_URL;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokCreationPlaywrightHelper {

    private final PlaywrightWaiter playwrightWaiter;

    private final MailTmService mailTmService;

    private final TikTokCaptchaSolver tikTokCaptchaSolver;

    private final TikTokService tikTokService;

    public void processSignUp(PlaywrightDto playwrightDto, TikTokAccount tikTokAccount) {
        Page page = playwrightDto.getPage();

        log.info("Opening browser, navigating to TikTok");
        processBrowserNavigation(playwrightDto);

        log.info("Starting account creation");
        processFirstStepRegistration(page);
        processSecondStepRegistration(page, tikTokAccount);
        processThirdStepRegistration(page, tikTokAccount, true);
        log.info("TikTok account successfully created by email {}", tikTokAccount.getEmail());
    }

    private void processBrowserNavigation(PlaywrightDto playwrightDto) {
        log.info("Processing browser navigation");
        Page page = playwrightDto.getPage();

        page.navigate(TIKTOK_SIGN_UP_DUCK_DUCK_GO_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

        playwrightWaiter.waitForSelectorAndAct(40_000, page.locator(HOME_SIGN_UP_SPAN), locator -> {
            if (locator.isVisible()) {
                locator.click();
            }
        });

        playwrightWaiter.waitForSelector(40_000, page.locator(CONTINUE_WITH_FACEBOOK_DIV));
    }

    private void processFirstStepRegistration(Page page) {
        log.info("Processing step one registration");

        waitRandomlyInRange(1200, 2200);
        page.click(SIGN_UP_USE_PHONE_OR_EMAIL_DIV);
        waitRandomlyInRange(1200, 2300);

        page.waitForSelector(SIGN_UP_WITH_EMAIL_TEXT);
        waitRandomlyInRange(1000, 1700);
        page.click(SIGN_UP_WITH_EMAIL_TEXT);
    }

    private void processSecondStepRegistration(Page page, TikTokAccount tikTokAccount) {
        log.info("Processing step two registration");
        LocalDate dotDate = LocalDate.parse(tikTokAccount.getBio().getDob().getDate().substring(0, 10), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        waitRandomlyInRange(1200, 1600);
        page.click(MONTH_DIV);
        waitRandomlyInRange(1200, 2000);
        page.click(selectMonth(Month.of(dotDate.getMonthValue())));

        waitRandomlyInRange(1000, 1500);
        page.click(DAY_DIV);
        waitRandomlyInRange(1100, 1900);
        page.click(selectDay(dotDate.getDayOfMonth()));

        waitRandomlyInRange(1200, 1800);
        page.click(YEAR_DIV);
        waitRandomlyInRange(1000, 1300);
        page.click(selectYear(Math.min(dotDate.getYear(), 2007)));

        waitRandomlyInRange(1000, 1500);
        page.fill(SIGN_UP_EMAIL_INPUT, tikTokAccount.getEmail());

        waitRandomlyInRange(1700, 2500);
        page.fill(PASSWORD_INPUT, tikTokAccount.getPassword());

        waitRandomlyInRange(900, 1700);
        handleSendCodeAction(tikTokAccount, page, true);
        waitRandomlyInRange(1200, 1700);

        try {
            String codeFromGeneratedEmail = mailTmService.getCodeFromGeneratedEmail(tikTokAccount.getEmail(), tikTokAccount.getPassword());
            waitRandomlyInRange(1300, 1900);
            page.fill(CODE_INPUT, codeFromGeneratedEmail);
        } catch (Exception e) {
            throw new TikTokCreationException(tikTokAccount, "Error while getting code from email");
        }

        waitRandomlyInRange(1800, 2100);
        page.click(NEXT_BUTTON);

        waitForLoadingOrThrow(page);
        handleSuspiciousActivity(page, tikTokAccount);
    }

    private void waitForLoadingOrThrow(Page page) {
        int maxAttempts = 40;
        while (page.locator(LOADING_CIRCLE_SVG).isVisible() && maxAttempts-- != 0) {
            waitSafely(1000);
        }

        if (maxAttempts == 0) {
            throw new TikTokCreationException("TikTok failed sending verification code to email");
        }

        page.waitForLoadState(LoadState.DOMCONTENTLOADED);
    }

    private void processThirdStepRegistration(Page page, TikTokAccount tikTokAccount, boolean retryProcess) {
        log.info("Processing step three registration");

        page.waitForSelector(USERNAME_INPUT);
        page.fill(USERNAME_INPUT, tikTokAccount.getUsername());
        waitRandomlyInRange(1000, 1700);

        handleUsernameNotAvailable(page, tikTokAccount, retryProcess);

        page.waitForSelector(SIGN_UP_BUTTON);
        page.click(SIGN_UP_BUTTON);

        waitForLogin(page);
    }

    private void handleUsernameNotAvailable(Page page, TikTokAccount tikTokAccount, boolean retryProcess) {
        playwrightWaiter.waitForSelectorAndAct(7_000, page.locator(USERNAME_NOT_AVAILABLE_CLASS), locator -> {
            if (locator.isVisible()) {
                if (page.locator(SUGGESTED_CLASS).isVisible()) {
                    page.locator(SUGGESTED_ELEMENTS_UL).first().click();
                } else {
                    if (retryProcess) {
                        tikTokService.update(
                                tikTokAccount.getId(),
                                UpdateAccountRequest.builder().username(tikTokService.generateUsername(tikTokAccount.getEmail())).build()
                        );
                        processThirdStepRegistration(page, tikTokAccount, false);
                    }
                }
            }
        });
    }

    private void waitForLogin(Page page) {
        playwrightWaiter.waitForSelectorAndAct(30_000, page.locator(LOGGED_IN_TEXT), locator -> {
            if (!locator.isVisible()) {
                log.warn("Couldn't input username, skipping...");
                page.click(SKIP_TEXT);
            }
        });

        page.waitForLoadState(LoadState.LOAD, new Page.WaitForLoadStateOptions().setTimeout(60_000));
    }

    private void handleSuspiciousActivity(Page page, TikTokAccount tikTokAccount) {
        List<String> suspiciousActivitySelectors = List.of(APP_SUGGESTION_TEXT, PLEASE_TRY_AGAIN_SPAN, VERIFICATION_CODE_EXPIRED_SPAN);
        for (String suspiciousActivitySelector : suspiciousActivitySelectors) {
            if (page.locator(suspiciousActivitySelector).isVisible()) {
                throw new TikTokBlockActivityException(tikTokAccount, "TikTok blocked bot activity, suggesting to open app instead of web");
            }
        }
    }

    private void handleSendCodeAction(TikTokAccount tikTokAccount, Page page, boolean retryProcess) {
        page.focus(SEND_CODE_ENABLED_BUTTON);
        waitRandomlyInRange(1300, 1700);

        page.click(SEND_CODE_ENABLED_BUTTON);
        waitRandomlyInRange(1300, 1700);

        if (!page.locator(SEND_CODE_DISABLED_BUTTON).isVisible()) {
            log.info("Send didn't work for the first time reclicking");
            page.click(SEND_CODE_ENABLED_BUTTON);
            waitRandomlyInRange(1300, 1700);
        }

        try {
            log.info("Waiting for sending");
            int attempts = 100;
            while (page.locator(LOADING_CIRCLE_SVG).isVisible()) {
                waitSafely(1000);

                if (page.locator(CAPTCHA_ID).isVisible()) {
                    try {
                        playwrightWaiter.waitForSelector(15_000, page.locator(CAPTCHA_IMG));
                        tikTokCaptchaSolver.solve(page);
                        return;
                    } catch (Exception e) {
                        throw new TikTokCaptchaException(tikTokAccount, "Tik tok rotation captcha not solved");
                    }
                } else if (page.locator(CAPTCHA_ID).isVisible() && attempts-- == 0) {
                    throw new TikTokCreationException("TikTok failed to send verification code to email");
                }
            }

            if (page.getByText(MAXIMUM_ATTEMPTS_REACHED_TEXT).isVisible()) {
                throw new TikTokBlockActivityException(tikTokAccount, "Tik Tok blocked sending code action");
            }

            if (page.locator(SEND_CODE_ENABLED_BUTTON).isVisible()) {
                if (retryProcess) {
                    handleSendCodeAction(tikTokAccount, page, false);
                    return;
                }
                throw new TikTokBlockActivityException(tikTokAccount, "Tik Tok blocked sending code action");
            }

            log.info("Code successfully sent to the mail");

        } catch (PlaywrightException e) {
            log.error(e.getMessage());
            throw new TikTokBlockActivityException(tikTokAccount, "Tik tok denied sending code to the email");
        }
    }

    public boolean isLoggedInByPopup(Page page) {
        return playwrightWaiter.waitForSelector(12_000, page.locator(ALREADY_LOGGED_IN_TEXT));
    }
}
