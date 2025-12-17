package com.yves_gendron.automation_tiktok.system.client.ip_api;

import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.system.client.ip_api.common.dto.GetProxyAddressResponse;
import com.yves_gendron.automation_tiktok.system.client.ip_api.common.exception.IpApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitSafely;

@Slf4j
@Service
@RequiredArgsConstructor
public class IpApiService {

    private final IpApiClient ipApiClient;

    public GetProxyAddressResponse tryToGetProxyAddress(Proxy proxy) {
        GetProxyAddressResponse proxyAddress;
        for (int i = 0; i < 3; i++) {
            try {
                proxyAddress = ipApiClient.getProxyAddress(proxy);

                if (proxyAddress != null) {
                    return proxyAddress;
                }
            } catch (IpApiException e) {
                waitSafely(1000);
                log.warn("Retrying to get proxy address {}/3", i);
            }
        }

        throw new RuntimeException("Failed to retrieve proxy address");
    }

}
