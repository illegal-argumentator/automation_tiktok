package com.yves_gendron.automation_tiktok.domain.tiktok.service.proxy;

import com.yves_gendron.automation_tiktok.common.command.ActionProxyCommand;
import com.yves_gendron.automation_tiktok.common.helper.ProxyHelper;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyNotAvailableException;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyService;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyVerifier;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.ProxyFilterRequest;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.UpdateProxyRequest;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import com.yves_gendron.automation_tiktok.domain.tiktok.web.dto.UpdateAccountRequest;
import com.yves_gendron.automation_tiktok.system.client.nst.NstBrowserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TikTokActionProxyCommand implements ActionProxyCommand {

    private final TikTokService tikTokService;

    private final NstBrowserClient nstBrowserClient;

    private final ProxyHelper proxyHelper;

    private final ProxyService proxyService;

    private final ProxyVerifier proxyVerifier;

    @Override
    public void executeActiveProxy(String accountId) {
        TikTokAccount tikTokAccount = tikTokService.findById(accountId);
        boolean verifiedProxy = proxyVerifier.verifyProxy(tikTokAccount.getProxy());

        if (!verifiedProxy) {
            log.info("Trying to find available proxy");
            try {
                Proxy proxy = executeAvailableProxy();
                log.info("Found proxy {} for account {}, replacing", proxy.getId(), accountId);

                tikTokService.update(accountId, UpdateAccountRequest.builder().proxy(proxy).build());
                nstBrowserClient.updateProfileProxy(tikTokAccount.getNstProfileId(), proxy);
            } catch (Exception e) {
                log.error("No proxies available", e);
                throw new ProxyNotAvailableException("Mobile proxy is not verified for this account");
            }
        } else if (!tikTokAccount.getProxy().isVerified()) {
            proxyService.update(tikTokAccount.getProxy().getId(), UpdateProxyRequest.builder().verified(true).build());
        }
    }

    private Proxy executeAvailableProxy() {
        List<Proxy> proxies = proxyService.findAll();
        if (proxies.isEmpty()) {
            throw new ProxyNotAvailableException("No proxies found");
        }

//        List<Proxy> accessibleProxies = proxyHelper.getAccessibleProxiesWithLimit(proxies, 1);
//        return accessibleProxies.getFirst();
        String forcedProxyId = "0a996cc5-9287-45a5-a974-191c86e58c44";

        return proxies.stream()
                .filter(p -> forcedProxyId.equals(p.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new ProxyNotAvailableException("Forced proxy not found: " + forcedProxyId)
                );
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
