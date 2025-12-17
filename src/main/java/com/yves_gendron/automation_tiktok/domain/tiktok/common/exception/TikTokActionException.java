package com.yves_gendron.automation_tiktok.domain.tiktok.common.exception;

import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import lombok.Getter;

@Getter
public class TikTokActionException extends RuntimeException {

    private final TikTokAccount tikTokAccount;

    public TikTokActionException(TikTokAccount tikTokAccount, String message) {
        super(message);
        this.tikTokAccount = tikTokAccount;
    }
}
