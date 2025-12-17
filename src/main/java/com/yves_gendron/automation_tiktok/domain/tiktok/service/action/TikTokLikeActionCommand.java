package com.yves_gendron.automation_tiktok.domain.tiktok.service.action;

import com.microsoft.playwright.Page;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokActionPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokCreationPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.LikeActionRequest;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightInitializer;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitRandomlyInRange;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.NEXT_VIDEO_BUTTON;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.selectLikeButton;

@Slf4j
@Service
public class TikTokLikeActionCommand extends TikTokActionCommand {

    private final TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper;

    private final TikTokService tikTokService;

    public TikTokLikeActionCommand(TikTokService tikTokService, PlaywrightWaiter playwrightWaiter, TikTokCreationPlaywrightHelper tikTokCreationPlaywrightHelper, PlaywrightInitializer playwrightInitializer, TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper) {
        super(tikTokService, playwrightWaiter, playwrightInitializer, tikTokCreationPlaywrightHelper, tikTokActionPlaywrightHelper);
        this.tikTokActionPlaywrightHelper = tikTokActionPlaywrightHelper;
        this.tikTokService = tikTokService;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest) {
        LikeActionRequest likeActionRequest = (LikeActionRequest) actionRequest;

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .likedPosts(tikTokAccount.getLikedPosts() + likeActionRequest.getActions())
                .executionMessage("")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(TikTokAccount tikTokAccount, PlaywrightDto playwrightDto, ActionRequest actionRequest) {
        try {
            LikeActionRequest likeActionRequest = (LikeActionRequest) actionRequest;

            log.info("Starting liking videos");

            Page page = playwrightDto.getPage();
            Random random = new Random();

            int liked = 0, videoIndex = 0;
            while (liked < likeActionRequest.getActions()) {
                boolean isDecidedToLike = random.nextBoolean();
                if (isDecidedToLike && !tikTokActionPlaywrightHelper.isVideoLive(page, videoIndex)) {
                    watchVideoAndLike(page, videoIndex);
                    liked++;
                }

                waitRandomlyInRange(1000, 3000);
                page.click(NEXT_VIDEO_BUTTON);
                videoIndex++;
            }
        } finally {
            playwrightDto.getAutoCloseables().forEach(ac -> {
                try {
                    ac.close();
                } catch (Exception e) {
                    log.error("Failed to close resource", e);
                }
            });
        }
    }

    @Override
    public Action getAction() {
        return Action.LIKE;
    }

    private void watchVideoAndLike(Page page, int videoIndex) {
        waitRandomlyInRange(2000, 5000);
        page.click(selectLikeButton(videoIndex));
    }
}
