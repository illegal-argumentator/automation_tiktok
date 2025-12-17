package com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.handler.service;

import com.yves_gendron.automation_tiktok.common.type.Action;
import com.yves_gendron.automation_tiktok.common.type.Status;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokActionException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokBlockActivityException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCaptchaException;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCreationException;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokExceptionService {

    private final TikTokService tikTokService;

    public String handleTikTokCaptchaException(TikTokCaptchaException e) {
        TikTokAccount tikTokAccount = e.getTikTokAccount();
        log.warn("Captcha appeared on account: {}. Tried to solve but {}", tikTokAccount.getEmail(), e.getMessage());

        updateActionOrStatusOnFail(tikTokAccount, "Captcha appearance");
        return "Captcha appearance";
    }

    public String handleTikTokBlockActivityException(TikTokBlockActivityException e) {
        TikTokAccount tikTokAccount = e.getTikTokAccount();
        updateActionOrStatusOnFail(tikTokAccount, e.getMessage());
        return e.getMessage();
    }

    public String handleTikTokActionException(TikTokActionException e) {
        updateActionOrStatusOnFail(e.getTikTokAccount(), e.getMessage());
        return e.getMessage();
    }

    public String handleTikTokCreationException(TikTokCreationException e) {
        if (e.getTikTokAccount() != null) {
            updateActionOrStatusOnFail(e.getTikTokAccount(), e.getMessage());
        }
        return e.getMessage();
    }

    private void updateActionOrStatusOnFail(TikTokAccount tikTokAccount, String executionMessage) {
        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .executionMessage(executionMessage)
                .build();

        if (tikTokAccount.getStatus() == Status.IN_PROGRESS) {
            updateAccountRequest.setStatus(Status.FAILED);
        }
        if (Optional.ofNullable(tikTokAccount.getAction()).isPresent() && tikTokAccount.getAction().isActable()) {
            updateAccountRequest.setAction(Action.FAILED);
        }

        tikTokService.update(tikTokAccount.getId(), updateAccountRequest);
    }
}
