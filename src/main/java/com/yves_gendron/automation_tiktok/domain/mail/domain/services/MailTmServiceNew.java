package com.yves_gendron.automation_tiktok.domain.mail.domain.services;

import com.yves_gendron.automation_tiktok.common.utils.OkHttpUtil;
import com.yves_gendron.automation_tiktok.domain.mail.db.entites.MailEntity;
import com.yves_gendron.automation_tiktok.domain.mail.db.repositories.MailRepository;
import com.yves_gendron.automation_tiktok.domain.mail.db.searches.MailSearch;
import com.yves_gendron.automation_tiktok.domain.mail.db.dto.*;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
class MailTmServiceNew implements MailService {

    private static final String BASE_URL = "https://api.mail.tm";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final OkHttpUtil OK_HTTP_UTIL = new OkHttpUtil(OBJECT_MAPPER, OK_HTTP_CLIENT);

    private final MailRepository mailRepository;

    @Override
    public String getEmail() {
        String domain = fetchDomain();
        String email = "user" + System.nanoTime() + "@" + domain;
        String password = "secret123";

        createAccount(email, password);
        String token = getToken(email, password);

        var entity = MailEntity.builder()
                .email(email)
                .password(password)
                .provider("MAIL_TM")
                .accessToken(token)
                .createdAt(OffsetDateTime.now())
                .build();

        log.info("Saving mail entity: {}", email);
        mailRepository.save(entity);
        log.info("Mail entity saved: {}", email);
        return email;
    }

    @Override
    public String retrieveCodeFromMessage(String email, OffsetDateTime date) {
        log.info("Looking for mail entity by email: {}", email);
        var search = MailSearch.builder().email(email).build();
        log.info("MailRepository count: {}", mailRepository.count());
        var entity = mailRepository.findAll(search, Pageable.ofSize(1))
                .stream()
                .findFirst()
                .orElseThrow();

        Request request = new Request.Builder()
                .url(BASE_URL + "/messages")
                .header("Authorization", "Bearer " + entity.getAccessToken())
                .build();

        try {
            String responseJson = OK_HTTP_UTIL.handleApiRequest(request);
            MailTmMessagesResponse response = OBJECT_MAPPER.readValue(responseJson, MailTmMessagesResponse.class);

            if (response == null || response.messages() == null) {
                throw new RuntimeException("No messages from mail.tm");
            }

            return response.messages().stream()
                    .filter(m -> m.createdAt().isAfter(date))
                    .map(this::extractCode)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElseThrow();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving messages from mail.tm", e);
        }
    }

    private String fetchDomain() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/domains")
                    .build();

            String responseJson = OK_HTTP_UTIL.handleApiRequest(request);
            MailTmDomainsResponse response = OBJECT_MAPPER.readValue(responseJson, MailTmDomainsResponse.class);
            if (response == null || response.members() == null || response.members().isEmpty()) {
                throw new RuntimeException("No available mail.tm domains (hydra:member is empty)");
            }

            return response.members().get(0).domain();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching mail.tm domains", e);
        }
    }

    private static void createAccount(String email, String password) {
        try {
            String jsonBody = OBJECT_MAPPER.writeValueAsString(new MailTmAccountRequest(email, password));

            Request request = new Request.Builder()
                    .url(BASE_URL + "/accounts")
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                    .build();

            String response = OK_HTTP_UTIL.handleApiRequest(request);
            System.out.println(response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

//    public static void main(String[] args) {
//        createAccount("jahdgjkeweg2376@virgilian.com", "Qwerty1234@");
//    }

    private String getToken(String email, String password) {
        try {
            String jsonBody = OBJECT_MAPPER.writeValueAsString(
                    new MailTmTokenRequest(email, password)
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/token")
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                    .build();
            String responseJson = OK_HTTP_UTIL.handleApiRequest(request);
            MailTmTokenResponse response =
                    OBJECT_MAPPER.readValue(responseJson, MailTmTokenResponse.class);

            if (response == null || !StringUtils.hasText(response.token())) {
                throw new RuntimeException("Failed to retrieve mail.tm token");
            }

            return response.token();
        } catch (Exception e) {
            throw new RuntimeException("Error while retrieving mail.tm token", e);
        }
    }

    @Nullable
    private String extractCode(MailTmMessagesResponse.Message message) {
        String subject = message.subject();
        if (!StringUtils.hasText(subject)) {
            return null;
        }

        if (subject.matches("^\\d{6}.*")) {
            String code = subject.substring(0, 6);
            log.info("OTP retrieved from mail.tm: {}", code);
            return code;
        }
        return "null";
    }
}