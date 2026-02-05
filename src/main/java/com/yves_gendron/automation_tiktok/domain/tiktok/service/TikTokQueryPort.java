package com.yves_gendron.automation_tiktok.domain.tiktok.service;

import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;

import java.util.List;

public interface TikTokQueryPort {

    List<TikTokAccount> getAllWithVideoWorkflow();

}
