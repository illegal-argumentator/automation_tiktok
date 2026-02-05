package com.yves_gendron.automation_tiktok.domain.tiktok.service.action;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.utils.PlayWrightUtils;
import com.yves_gendron.automation_tiktok.common.utils.ThreadUtils;
import com.yves_gendron.automation_tiktok.common.utils.tries.TryUtils;
import com.yves_gendron.automation_tiktok.domain.file.video.model.Video;
import com.yves_gendron.automation_tiktok.domain.file.video.service.VideoService;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokActionException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCreationException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokActionPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokCreationPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.VideoActionRequest;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightInitializer;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Random;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitRandomlyInRange;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokConstants.TIKTOK_BASE_URL;
import static com.yves_gendron.automation_tiktok.domain.tiktok.common.constants.TikTokSelectors.*;

@Slf4j
@Service
public class TikTokVideoActionCommand extends TikTokActionCommand {

    private final TikTokService tikTokService;

    private final PlaywrightWaiter playwrightWaiter;

    private final VideoService videoService;

    public TikTokVideoActionCommand(
            TikTokService tikTokService,
            PlaywrightWaiter playwrightWaiter,
            VideoService videoService,
            PlaywrightInitializer playwrightInitializer,
            TikTokCreationPlaywrightHelper tikTokCreationPlaywrightHelper,
            TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper
    ) {
        super(tikTokService, playwrightWaiter, playwrightInitializer, tikTokCreationPlaywrightHelper, tikTokActionPlaywrightHelper);
        this.tikTokService = tikTokService;
        this.playwrightWaiter = playwrightWaiter;
        this.videoService = videoService;
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .publishedPosts(tikTokAccount.getPublishedPosts() + 1)
                .executionMessage("")
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    protected void startAction(TikTokAccount tikTokAccount, PlaywrightDto playwrightDto, ActionRequest actionRequest) {
        log.info("Starting publishing video");

        VideoActionRequest videoActionRequest = (VideoActionRequest) actionRequest;

        Page page = playwrightDto.getPage();
        uploadVideo(tikTokAccount, videoActionRequest, page);

        log.info("Video uploaded successfully");

        ThreadUtils.sleep(2000, 2500);
        log.info("Close dialogs if any");
        for (int i = 0, size = new Random().nextInt(2,5); i < size; i++) {
            page.keyboard().press("Escape");
            ThreadUtils.sleep(200, 250);
            if (page.locator(".common-modal-close").count() > 0) {
                TryUtils.tryRun(() -> PlayWrightUtils
                        .executeClick(page.locator(".common-modal-close").first(), page));
            }
            if (page.locator(".common-modal-close svg").count() > 0) {
                TryUtils.tryRun(() -> PlayWrightUtils
                        .executeClick(page.locator(".common-modal-close svg").first(), page));
            }
        }

        ThreadUtils.sleep(1500, 2500);

        playwrightWaiter.waitForSelectorAndAct(page.locator(POST_BUTTON), locator -> {
            if (locator.isVisible()) {
                PlayWrightUtils.executeClick(locator, page);
            }
        });
        TryUtils.tryRun(() -> playwrightWaiter.waitForSelectorAndAct(page.locator(POST_NOW_BUTTON), locator -> {
            if (locator.isVisible()) {
                PlayWrightUtils.executeClick(locator, page);
            }
        }));

        ThreadUtils.sleep(2500, 2500);
        log.info("Navigating to base url to confirm publication");
        ThreadUtils.sleep(5500, 5500);

        page.navigate(TIKTOK_BASE_URL, new Page.NavigateOptions().setWaitUntil(WaitUntilState.COMMIT));

        playwrightWaiter.waitForSelectorAndAct(page.locator(VIDEO_PUBLISHED_TEXT), locator -> {
            if (locator.isVisible()) {
                log.info("Video successfully published");
            }
        });
        ThreadUtils.sleep(1500, 2500);
    }

    private void uploadVideo(TikTokAccount tikTokAccount, VideoActionRequest videoActionRequest, Page page) {
        Video video = videoService.findByIdOrThrow(videoActionRequest.getVideoId());

        try {
            waitRandomlyInRange(1100, 1500);
            page.click(UPLOAD_DIV);
            page.waitForLoadState();

            waitRandomlyInRange(1100, 1500);
            Path filePath = parseVideoToFile(video);
            page.setInputFiles(FILE_INPUT, filePath);
        } catch (SQLException | IOException e) {
            log.error(e.getMessage());
            throw new TikTokCreationException(tikTokAccount, "Video upload failed");
        }

        playwrightWaiter.waitForSelectorAndAct(50000, page.locator(UPLOADED_TEXT), locator -> {
            if (!locator.isVisible()) {
                throw new TikTokActionException(tikTokAccount, "Video upload took too long");
            }
        });
    }

    private Path parseVideoToFile(Video video) throws SQLException, IOException {
        File tempFile = File.createTempFile("video-", ".mp4");
        tempFile.deleteOnExit();

        try (InputStream is = video.getDataBlob().getBinaryStream();
             OutputStream os = new FileOutputStream(tempFile)) {
            is.transferTo(os);
        }

        return tempFile.toPath();
    }


    @Override
    public Action getAction() {
        return Action.VIDEO;
    }
}
