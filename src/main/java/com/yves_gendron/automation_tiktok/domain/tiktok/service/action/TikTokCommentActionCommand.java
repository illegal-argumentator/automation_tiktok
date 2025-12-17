package com.yves_gendron.automation_tiktok.domain.tiktok.service.action;

import com.microsoft.playwright.Page;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokActionPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokCreationPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.CommentActionRequest;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightInitializer;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitRandomlyInRange;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
public class TikTokCommentActionCommand extends TikTokActionCommand {

    private final TikTokService tikTokService;

    private final TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper;

    private static final String COMMENT_TEXT = "lol";

    public TikTokCommentActionCommand(
            TikTokService tikTokService,
            PlaywrightWaiter playwrightWaiter,
            PlaywrightInitializer playwrightInitializer,
            TikTokCreationPlaywrightHelper tikTokCreationPlaywrightHelper,
            TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper
    ) {
        super(tikTokService, playwrightWaiter, playwrightInitializer, tikTokCreationPlaywrightHelper, tikTokActionPlaywrightHelper);
        this.tikTokService = tikTokService;
        this.tikTokActionPlaywrightHelper = tikTokActionPlaywrightHelper;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest) {
        CommentActionRequest commentActionRequest = (CommentActionRequest) actionRequest;

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .commentedPosts(tikTokAccount.getCommentedPosts() + commentActionRequest.getActions())
                .executionMessage("")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(TikTokAccount tikTokAccount, PlaywrightDto playwrightDto, ActionRequest actionRequest) {
        try {
            CommentActionRequest commentActionRequest = (CommentActionRequest) actionRequest;

            log.info("Starting commenting videos");

            Page page = playwrightDto.getPage();
            openCommentSection(page);

            Random random = new Random();

            int commented = 0, videoIndex = 0;
            while (commented < commentActionRequest.getActions()) {
                boolean isDecidedToLike = random.nextBoolean();
                if ((!tikTokActionPlaywrightHelper.isVideoLive(page, videoIndex) && isDecidedToLike) || !tikTokActionPlaywrightHelper.isCommentsDisabled(page)) {
                    watchVideoAndComment(page);
                    commented++;
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
        return Action.COMMENT;
    }

    private void openCommentSection(Page page) {
        page.click(selectCommentButton(0));
        waitRandomlyInRange(1000, 3000);
    }

    private void watchVideoAndComment(Page page) {
        waitRandomlyInRange(2000, 5000);
        page.focus(COMMENT_TEXT_DIV);
        waitRandomlyInRange(1000, 3000);

        page.fill(COMMENT_TEXT_DIV, COMMENT_TEXT);
        waitRandomlyInRange(1000, 3000);

        page.click(POST_COMMENT_DIV);
        waitRandomlyInRange(1000, 3000);
    }
}