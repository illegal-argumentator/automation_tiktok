package com.yves_gendron.automation_tiktok.system.service.captcha;

import com.microsoft.playwright.Page;

public interface CaptchaSolver {

    void solve(Page page);

}
