package com.yves_gendron.automation_tiktok.common.helper;

import com.yves_gendron.automation_tiktok.config.AppProps;
import com.yves_gendron.automation_tiktok.domain.proxy.common.exception.ProxyNotAvailableException;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyService;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyVerifier;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.UpdateProxyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProxyHelper {

    private final ProxyVerifier proxyVerifier;

    private final AppProps appProps;

    private final ProxyService proxyService;

    private final OkHttpClient okHttpClient;

    public List<Proxy> getAccessibleProxiesWithLimit(List<Proxy> proxies, int limit) {
        List<Proxy> accessibleProxies = new ArrayList<>();

        for (Proxy proxy : proxies) {
            if (accessibleProxies.size() == limit) {
                break;
            }

            boolean verifiedProxy = updateProxyVerification(proxy);
            if (!verifiedProxy) {
                continue;
            }

            if(proxy.getAccountsLinked() >= appProps.getAccountsPerProxy()) {
                rotateProxyByUrl(proxy.getAutoRotationLink());
                proxyService.update(proxy.getId(), UpdateProxyRequest.builder().accountsLinked(0).verified(true).build());
            }

            accessibleProxies.add(proxy);
        }

        if (retrieveMaximumProxyUsage(accessibleProxies) < limit) {
            throw new ProxyNotAvailableException("Not enough accessible proxies");
        }

        return accessibleProxies;
    }

    private int retrieveMaximumProxyUsage(List<Proxy> proxies) {
        return proxies.stream()
                .mapToInt(proxy -> appProps.getAccountsPerProxy() - proxy.getAccountsLinked())
                .sum();
    }

    private boolean updateProxyVerification(Proxy proxy) {
        boolean verifiedProxy = proxyVerifier.verifyProxy(proxy);

        if (!verifiedProxy) {
            if (proxy.isVerified()) {
                proxyService.update(proxy.getId(), UpdateProxyRequest.builder().verified(false).build());
            }
        } else {
            if (!proxy.isVerified()) {
                proxyService.update(proxy.getId(), UpdateProxyRequest.builder().verified(true).build());
            }
        }

        return verifiedProxy;
    }

    private void rotateProxyByUrl(String rotationLink) {
            Request request = new Request.Builder()
                    .get()
                    .url(rotationLink)
                    .build();
        try (Response ignored = okHttpClient.newCall(request).execute()) {
            String PROXY_ID_PARAMETER = "uuid";
            log.info("Rotating proxy: {}.", rotationLink.substring(rotationLink.indexOf(PROXY_ID_PARAMETER) + 1));
        } catch (IOException e) {
            log.error("NstBrowserException: {}", e.getMessage());
        }
    }
}
