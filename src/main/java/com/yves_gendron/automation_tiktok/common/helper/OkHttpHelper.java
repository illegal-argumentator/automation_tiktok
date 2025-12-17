package com.yves_gendron.automation_tiktok.common.helper;

import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;

@Slf4j
@Service
public class OkHttpHelper {

    public OkHttpClient buildClientWithProxy(Proxy proxy) {
        return new OkHttpClient.Builder()
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())))
                .proxyAuthenticator((route, response) -> response.request().newBuilder()
                        .header("Proxy-Authorization", Credentials.basic(proxy.getUsername(), proxy.getPassword()))
                        .build())
                .build();
    }

    public String buildResponseBodyOrThrow(Response response, String message) throws IOException {
        String responseBody = response.body() != null ? response.body().string() : "";
        if (!response.isSuccessful()) {
            log.error("Failed to proceed request: {} - {}, Body: {}", response.code(), response.message(), responseBody);
            throw new IOException(message);
        }
        return responseBody;
    }
}
