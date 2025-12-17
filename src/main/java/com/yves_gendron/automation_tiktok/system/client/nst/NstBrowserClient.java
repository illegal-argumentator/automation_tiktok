package com.yves_gendron.automation_tiktok.system.client.nst;

import com.yves_gendron.automation_tiktok.common.helper.OkHttpHelper;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.system.client.nst.common.dto.CreateProfileRequest;
import com.yves_gendron.automation_tiktok.system.client.nst.common.dto.CreateProfileResponse;
import com.yves_gendron.automation_tiktok.system.client.nst.common.dto.UpdateProfileProxyRequest;
import com.yves_gendron.automation_tiktok.system.client.nst.common.exception.NstBrowserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

import static com.yves_gendron.automation_tiktok.system.client.nst.common.dto.builder.CreateProfileRequestBuilder.buildCreateProfileRequest;

@Slf4j
@Service
@RequiredArgsConstructor
public class NstBrowserClient {

    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient;

    private final OkHttpHelper okHttpHelper;

    @Value("${nst-browser.api-key}")
    private String NST_BROWSER_API_KEY;

    @Value("${nst-browser.url}")
    private String NST_API;

    public CreateProfileResponse createProfile(String profileName, Proxy proxy) {
        try {
            CreateProfileRequest createProfileRequest = buildCreateProfileRequest(profileName, proxy);

            String json = objectMapper.writeValueAsString(createProfileRequest);

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, json);

            Request request = new Request.Builder()
                    .url(NST_API + "/profiles")
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", NST_BROWSER_API_KEY)
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.warn("Response of creating nst profile failed");
            }

            CreateProfileResponse createProfileResponse = objectMapper.readValue(Objects.requireNonNull(response.body()).string(), CreateProfileResponse.class);

            response.close();
            return createProfileResponse;
        } catch (IOException e) {
            log.error("NstBrowserException: {}", e.getMessage());
            throw new NstBrowserException("Couldn't create profile in Nst browser");
        }
    }

    public void updateProfileProxy(String profileId, Proxy proxy) {
        try{
            MediaType mediaType = MediaType.parse("application/json");
            UpdateProfileProxyRequest updateProfileProxyRequest = UpdateProfileProxyRequest.builder()
                    .url("http://%s:%s@%s:%d".formatted(
                            proxy.getUsername(),
                            proxy.getPassword(),
                            proxy.getHost(),
                            proxy.getPort()
                    )).build();

            String json = objectMapper.writeValueAsString(updateProfileProxyRequest);
            RequestBody body = RequestBody.create(mediaType, json);

            Request request = new Request.Builder()
                    .url(NST_API + "/profiles/" + profileId + "/proxy")
                    .method("PUT", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("x-api-key", NST_BROWSER_API_KEY)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            okHttpHelper.buildResponseBodyOrThrow(response, "NstApi error: " + response.code() + " - " + response.message());

            response.close();
        } catch (IOException e) {
            log.error("NstBrowserException: {}", e.getMessage());
        }
    }
}
