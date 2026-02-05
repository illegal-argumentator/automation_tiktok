package com.yves_gendron.automation_tiktok.domain.tiktok.service.action;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;
import com.yves_gendron.automation_tiktok.common.command.ActionCommand;
import com.yves_gendron.automation_tiktok.common.exception.ApplicationAlreadyInProgressException;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.common.utils.PlayWrightUtils;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokActionException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokActionPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokCreationPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Workflow;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.client.nst.common.exception.NstBrowserException;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.VideoActionRequest;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightInitializer;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokConstants.TIKTOK_SIGN_IN_DUCK_DUCK_GO_URL;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.HOME_L0G_IN_SPAN;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.SELECT_ADD_DIV;

@Slf4j
@RequiredArgsConstructor
public abstract class TikTokActionCommand implements ActionCommand {

    private final TikTokService tikTokService;

    private final PlaywrightWaiter playwrightWaiter;

    private final PlaywrightInitializer playwrightInitializer;

    private final TikTokCreationPlaywrightHelper tikTokCreationPlaywrightHelper;

    private final TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper;

    @Override
    public void executeAction(String accountId, Action action, ActionRequest actionRequest) {
        TikTokAccount tikTokAccount = getAccountIfNotInActionAndNotInProgress(accountId);
        tikTokService.update(tikTokAccount.getId(), UpdateAccountRequest.builder().action(action).build());
        initializeNstAndStartAction(tikTokAccount, actionRequest);
        tearDownAccountAction(tikTokAccount, actionRequest);
        log.info("Successfully acted");
    }

    protected void initializeNstAndStartAction(TikTokAccount tikTokAccount, ActionRequest actionRequest) {
        // TODO should be refactored
        if (actionRequest instanceof VideoActionRequest request) {
            if (request.getUploadAt() != null) {
                Workflow workflow = new Workflow();
                workflow.setVideoSetting(new Workflow.VideoSetting(request.getUploadAt()));
                tikTokService.update(tikTokAccount.getId(), UpdateAccountRequest.builder().workflow(workflow).build());

                return;
            }
        }

        PlaywrightDto playwrightDto = PlaywrightDto.builder().autoCloseables(List.of()).build();
        try {
            playwrightDto = playwrightInitializer.initBrowser(tikTokAccount.getNstProfileId());

            Page page = playwrightDto.getPage();

            log.info("Opening browser");
            page.bringToFront();
            page.navigate(TIKTOK_SIGN_IN_DUCK_DUCK_GO_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED).setTimeout(35_000));

            playwrightWaiter.waitForSelectorAndAct(15000, page.locator(HOME_L0G_IN_SPAN).first(), locator -> {
                if (locator.isVisible()) {
                    PlayWrightUtils.executeClick(locator, page);
                }
            });

            if (!tikTokCreationPlaywrightHelper.isLoggedInByPopup(page)) {
                log.info("User not signed in. Processing logging");
                tikTokActionPlaywrightHelper.processBrowserLogIn(page, tikTokAccount);
            }

            playwrightWaiter.waitForSelectorAndAct(15000, page.locator(SELECT_ADD_DIV), locator -> {
                if (locator.isVisible()) {
                    PlayWrightUtils.executeClick(locator, page);
                }
            });

            page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(35_000));

            startAction(tikTokAccount, playwrightDto, actionRequest);
        } catch (PlaywrightException e) {
            log.error(e.getMessage(), e);
            throw new TikTokActionException(tikTokAccount, "Unstable proxy connection, couldn't access the element");
        } catch (NstBrowserException e) {
            log.error(e.getMessage(), e);
            throw new TikTokActionException(tikTokAccount, "Nst Browser exception occurred. Probably plan exceeded");
        } finally {
            playwrightDto.close();
        }
    }

    private TikTokAccount getAccountIfNotInActionAndNotInProgress(String accountId) {
        TikTokAccount tikTokAccount = tikTokService.findById(accountId);
        if (tikTokAccount.getAction() == null) {
            return tikTokAccount;
        }

        if (tikTokAccount.getStatus() == Status.IN_PROGRESS
                || tikTokAccount.getStatus() == Status.FAILED
        ) {
            throw new ApplicationAlreadyInProgressException("This account is currently creating or creation failed");
        }

        if (tikTokAccount.getAction().isActable()) {
            throw new ApplicationAlreadyInProgressException("This account is already in action");
        }

        return tikTokAccount;
    }

    protected abstract void startAction(
            TikTokAccount tikTokAccount,
            PlaywrightDto playwrightDto,
            ActionRequest actionRequest);

    protected abstract void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest);

    @Override
    public abstract Action getAction();

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
