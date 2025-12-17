package com.yves_gendron.automation_tiktok.domain.tiktok.common.exception;

import com.yves_gendron.automation_tiktok.common.exception.CaptchaException;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import lombok.Getter;

@Getter
public class TikTokCaptchaException extends CaptchaException {

    private final TikTokAccount tikTokAccount;

    public TikTokCaptchaException(TikTokAccount tikTokAccount, String message) {
        super(message);
        this.tikTokAccount = tikTokAccount;
    }
}
