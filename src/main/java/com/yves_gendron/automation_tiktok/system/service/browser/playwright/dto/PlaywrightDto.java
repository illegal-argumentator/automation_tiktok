package com.yves_gendron.automation_tiktok.system.service.browser.playwright.dto;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.yves_gendron.automation_tiktok.common.utils.tries.TryUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlaywrightDto implements AutoCloseable{

    private List<AutoCloseable> autoCloseables;

    private String cdpUrl;

    private Page page;

    private BrowserContext browserContext;

    @Override
    public void close()  {
        if (CollectionUtils.isEmpty(autoCloseables)) {
            return;
        }
        for (AutoCloseable autoCloseable : autoCloseables) {
            TryUtils.tryRun(autoCloseable::close);
        }
    }
}
