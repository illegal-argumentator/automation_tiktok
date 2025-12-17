package com.yves_gendron.automation_tiktok.domain.tiktok.service.action;

import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.domain.file.photo.model.Photo;
import com.yves_gendron.automation_tiktok.domain.file.photo.service.PhotoService;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokActionPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.helper.TikTokCreationPlaywrightHelper;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.ActionRequest;
import com.yves_gendron.automation_tiktok.system.controller.dto.AvatarActionRequest;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightInitializer;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.PlaywrightWaiter;
import com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto.PlaywrightDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TikTokAvatarActionCommand extends TikTokActionCommand {

    private final TikTokService tikTokService;

    private final TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper;

    private final PhotoService photoService;

    public TikTokAvatarActionCommand(
            TikTokService tikTokService,
            PlaywrightWaiter playwrightWaiter,
            PlaywrightInitializer playwrightInitializer,
            TikTokCreationPlaywrightHelper tikTokCreationPlaywrightHelper,
            TikTokActionPlaywrightHelper tikTokActionPlaywrightHelper,
            PhotoService photoService
    ) {
        super(tikTokService, playwrightWaiter, playwrightInitializer, tikTokCreationPlaywrightHelper, tikTokActionPlaywrightHelper);
        this.tikTokService = tikTokService;
        this.tikTokActionPlaywrightHelper = tikTokActionPlaywrightHelper;
        this.photoService = photoService;
    }

    @Override
    protected void startAction(TikTokAccount tikTokAccount, PlaywrightDto playwrightDto, ActionRequest actionRequest) {
        AvatarActionRequest avatarActionRequest = (AvatarActionRequest) actionRequest;

        String avatarLink = photoService.buildImageLink(avatarActionRequest.getPhotoId());
        tikTokAccount.setAvatarLink(avatarLink);

        Photo photo = photoService.findById(avatarActionRequest.getPhotoId());
        tikTokActionPlaywrightHelper.processAvatarSetting(tikTokAccount, photo.getData(), playwrightDto);
    }

    @Override
    protected void tearDownAccountAction(TikTokAccount tikTokAccount, ActionRequest actionRequest) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .action(Action.ACTED)
                .executionMessage("")
                .avatarLink(tikTokAccount.getAvatarLink())
                .build();
        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }

    @Override
    public Action getAction() {
        return Action.AVATAR;
    }
}
