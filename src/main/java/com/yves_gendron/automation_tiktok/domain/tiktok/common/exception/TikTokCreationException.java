package com.yves_gendron.automation_tiktok.domain.tiktok.common.exception;

import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import lombok.Getter;

@Getter
public class TikTokCreationException extends RuntimeException {

    private TikTokAccount tikTokAccount;

    public TikTokCreationException(TikTokAccount tikTokAccount, String message) {
        super(message);
        this.tikTokAccount = tikTokAccount;
    }

    public TikTokCreationException(String message) {
        super(message);
    }
}
