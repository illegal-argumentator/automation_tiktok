package com.yves_gendron.automation_tiktok.domain.tiktok.service.proxy;

import com.yves_gendron.automation_tiktok.common.command.CreationProxyCommand;
import com.yves_gendron.automation_tiktok.common.helper.ProxyHelper;
import com.yves_gendron.automation_tiktok.common.type.Platform;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyNotAvailableException;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyService;
import com.yves_gendron.automation_tiktok.domain.tiktok.common.exception.TikTokCreationException;
import com.yves_gendron.automation_tiktok.system.controller.dto.CreateAccountsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TikTokCreationProxyCommand implements CreationProxyCommand {

    private final ProxyHelper proxyHelper;

    private final ProxyService proxyService;

    @Override
    public void executeAvailableProxies(CreateAccountsRequest createAccountsRequest) {
        List<Proxy> proxies = proxyService.findAll();

        if (proxies.isEmpty()) {
            throw new ProxyNotAvailableException("No proxies are available to start creation.");
        }

        try {
            proxyHelper.getAccessibleProxiesWithLimit(proxies, createAccountsRequest.getAmount());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new TikTokCreationException(e.getMessage());
        }
    }

    @Override
    public Platform getPlatform() {
        return Platform.TIKTOK;
    }
}
