package com.yves_gendron.automation_tiktok.system.client.mailtm;

import com.yves_gendron.automation_tiktok.common.helper.OkHttpHelper;
import com.yves_gendron.automation_tiktok.system.client.mailtm.dto.AccountRequest;
import com.yves_gendron.automation_tiktok.system.client.mailtm.dto.GetDomainsResponse;
import com.yves_gendron.automation_tiktok.system.client.mailtm.dto.GetMessagesResponse;
import com.yves_gendron.automation_tiktok.system.client.mailtm.dto.GetTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static com.yves_gendron.automation_tiktok.common.helper.WaitHelper.waitSafely;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailTmClient {

    private final ObjectMapper objectMapper;

    private final OkHttpHelper okHttpHelper;

    private final OkHttpClient okHttpClient;

    private static final String MAIL_TM_BASE_URL= "https://api.mail.tm";

    public void createAccount(String address, String password) {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/accounts";
            AccountRequest accountRequest = AccountRequest.builder()
                    .address(address)
                    .password(password)
                    .build();

            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(accountRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .post(requestBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            response = handleTooManyRequests(request, response);

            okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            waitSafely(1100);
        } catch (IOException e) {
            throw new RuntimeException("Exception while creating account in MailTm");
        }
    }

    public GetTokenResponse getToken(String address, String password) {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/token";
            AccountRequest accountRequest = AccountRequest.builder()
                    .address(address)
                    .password(password)
                    .build();

            RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(accountRequest), MediaType.get("application/json"));
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .post(requestBody)
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            response = handleTooManyRequests(request, response);

            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            return objectMapper.readValue(responseBody, GetTokenResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Exception while getting token from MailTm");
        }
    }

    public GetDomainsResponse getDomains() {
        try {
            String createAccountUrl = MAIL_TM_BASE_URL + "/domains";
            Request request = new Request.Builder()
                    .url(createAccountUrl)
                    .get()
                    .build();

            Response response = okHttpClient.newCall(request).execute();
            response = handleTooManyRequests(request, response);

            String responseBody = okHttpHelper.buildResponseBodyOrThrow(response, "MailTmApi error: " + response.code() + " - " + response.message());
            response.close();

            GetDomainsResponse getDomainsResponse = objectMapper.readValue(responseBody, GetDomainsResponse.class);
            if (getDomainsResponse.getTotalItems() == 0) {
                throw new RuntimeException("No domain found in MailTm");
            }

            return getDomainsResponse;
        } catch (IOException e) {
            throw new RuntimeException("Exception while getting domains from MailTm");
        }
    }

    public GetMessagesResponse getMessages(String token) {
        String url = MAIL_TM_BASE_URL + "/messages";
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .get()
                .build();

        try (Response initialResponse = okHttpClient.newCall(request).execute()) {
            Response response = handleTooManyRequests(request, initialResponse);

            String responseBody = okHttpHelper.buildResponseBodyOrThrow(
                    response,
                    "MailTmApi error: " + response.code() + " - " + response.message()
            );
            response.close();

            return objectMapper.readValue(responseBody, GetMessagesResponse.class);

        } catch (IOException e) {
            throw new RuntimeException("Exception while getting messages from MailTm", e);
        }
    }

    private Response handleTooManyRequests(Request request, Response response) throws IOException {
        int attempts = 3;

        while (attempts-- > 0 && response.code() == HttpStatus.TOO_MANY_REQUESTS.value()) {
            String retryAfterHeader = response.header(HttpHeaders.RETRY_AFTER);
            long waitTime = 1500;
            if (retryAfterHeader != null) {
                try {
                    waitTime = Long.parseLong(retryAfterHeader) * 1000;
                } catch (NumberFormatException ignored) {}
            }

            response.close();
            waitSafely(waitTime);
            response = okHttpClient.newCall(request).execute();
        }

        return response;
    }


}
