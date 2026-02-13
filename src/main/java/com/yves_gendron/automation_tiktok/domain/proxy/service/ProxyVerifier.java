package com.yves_gendron.automation_tiktok.domain.proxy.service;

import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitSafely;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProxyVerifier {

    private static final String IF_CONFIG_BASE_URL = "https://ifconfig.me/ip";

    public boolean verifyProxy(Proxy proxy, int retries) {
        int attempts = 1;
        try {
            for (int i = 0; i < retries; i++) {
                log.info("Trying to verify proxy {} ... {}", proxy.getUsername(), "%d/%d".formatted(attempts++, retries));
                boolean validProxy = processProxyValidation(proxy);
                if (validProxy) {
                    return true;
                }

                waitSafely(1500);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Failed proxy verification {}", proxy.getUsername());
            return false;
        }
        log.warn("Failed proxy verification {}", proxy.getUsername());
        return false;
    }

    public boolean verifyProxy(Proxy proxy) {
        return verifyProxy(proxy, 3);
    }



    private boolean processProxyValidation(Proxy proxy) {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())))
                .proxyAuthenticator((route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", Credentials.basic(proxy.getUsername(), proxy.getPassword()))
                        .build())
                .build();

        Request request = new Request.Builder()
                .url(IF_CONFIG_BASE_URL)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.isSuccessful() && response.body() != null;
        } catch (IOException e) {
            return false;
        }
    }
}
