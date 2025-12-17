package com.yves_gendron.automation_tiktok.system.service.browser.playwright;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.PlaywrightException;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class PlaywrightWaiter {

    public boolean waitForSelectorAndAct(Locator locator, Consumer<Locator> consumer) {
        return waitForSelectorAndAct(4000, locator, consumer);
    }

    public boolean waitForSelectorAndAct(int duration, Locator locator, Consumer<Locator> consumer) {
        boolean appeared = waitForSelector(duration, locator);

        consumer.accept(locator);

        return appeared;
    }

    public boolean waitForSelector(int duration, Locator locator) {
        boolean appeared;

        try {
            locator.waitFor(new Locator.WaitForOptions().setTimeout(duration));
            appeared = locator.isVisible(new Locator.IsVisibleOptions().setTimeout(duration));
        } catch (PlaywrightException e) {
            appeared = false;
        }

        return appeared;
    }
}
