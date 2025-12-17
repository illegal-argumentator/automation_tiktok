package com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaywrightDto {

    private List<AutoCloseable> autoCloseables;

    private String cdpUrl;

    private Page page;

    private BrowserContext browserContext;

}
