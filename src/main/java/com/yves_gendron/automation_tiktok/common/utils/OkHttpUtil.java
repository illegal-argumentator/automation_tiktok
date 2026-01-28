package com.yves_gendron.automation_tiktok.common.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OkHttpUtil {

    private final ObjectMapper objectMapper;

    private final OkHttpClient okHttpClient;

    public <T> T handleApiRequest(Request request, Class<T> responseTarget) {
        return objectMapper.readValue(handleApiRequest(request), responseTarget);
    }

    public String handleApiRequest(Request request) {
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String message = objectMapper.readValue(response.body().string(), Object.class).toString();
                logOkHttpUtilError(message);
            }

            return response.body().string();
        } catch (IOException e) {
            logOkHttpUtilError(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private void logOkHttpUtilError(String message) {
        log.error("OkHttpUtil: {}", message);
    }

}
