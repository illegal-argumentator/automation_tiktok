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
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.firstNonBlank;

@Slf4j
@Service
@RequiredArgsConstructor
class MailTmServiceNew implements MailService {

    private static final String BASE_URL = "https://api.mail.tm";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final OkHttpClient OK_HTTP_CLIENT = new OkHttpClient();
    private static final OkHttpUtil OK_HTTP_UTIL = new OkHttpUtil(OBJECT_MAPPER, OK_HTTP_CLIENT);

    private static final Pattern OTP_PATTERN = Pattern.compile("\\b(\\d{6})\\b");

    private final MailRepository mailRepository;

    // =========================
    // EMAIL CREATION
    // =========================
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

        return email;
    }

    // =========================
    // OTP RETRIEVAL (OLD LOGIC + BODY PARSING)
    // =========================
    @Override
    public String retrieveCodeFromMessage(String email, OffsetDateTime date) {
        log.info("Looking for OTP for email: {}", email);

        var entity = mailRepository.findAll(
                MailSearch.builder().email(email).build(),
                Pageable.ofSize(1)
        ).stream().findFirst().orElseThrow();

        long timeoutMs = 60_000;
        long start = System.currentTimeMillis();

        while (System.currentTimeMillis() - start < timeoutMs) {
            try {
                Request request = new Request.Builder()
                        .url(BASE_URL + "/messages")
                        .header("Authorization", "Bearer " + entity.getAccessToken())
                        .build();

                String messagesJson = OK_HTTP_UTIL.handleApiRequest(request);
                String code = extractCode(messagesJson, date, entity.getAccessToken());

                if (code != null) {
                    return code;
                }

                Thread.sleep(3000);

            } catch (Exception e) {
                log.warn("Waiting for OTP...", e);
            }
        }

        throw new RuntimeException("OTP code not received within timeout");
    }

    // =========================
    // DOMAIN
    // =========================
    private String fetchDomain() {
        try {
            Request request = new Request.Builder()
                    .url(BASE_URL + "/domains")
                    .build();

            String json = OK_HTTP_UTIL.handleApiRequest(request);
            MailTmDomainsResponse response =
                    OBJECT_MAPPER.readValue(json, MailTmDomainsResponse.class);

            return response.members().stream()
                    .map(MailTmDomainsResponse.Domain::domain)
                    .filter(d -> d.endsWith(".com"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No valid mail.tm domain"));

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch domain", e);
        }
    }

    // =========================
    // ACCOUNT
    // =========================
    private static void createAccount(String email, String password) {
        try {
            String body = OBJECT_MAPPER.writeValueAsString(
                    new MailTmAccountRequest(email, password)
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/accounts")
                    .post(RequestBody.create(body, MediaType.get("application/json")))
                    .build();

            OK_HTTP_UTIL.handleApiRequest(request);

        } catch (Exception e) {
            throw new RuntimeException("Failed to create mail.tm account", e);
        }
    }

    private String getToken(String email, String password) {
        try {
            String body = OBJECT_MAPPER.writeValueAsString(
                    new MailTmTokenRequest(email, password)
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/token")
                    .post(RequestBody.create(body, MediaType.get("application/json")))
                    .build();

            String json = OK_HTTP_UTIL.handleApiRequest(request);
            MailTmTokenResponse response =
                    OBJECT_MAPPER.readValue(json, MailTmTokenResponse.class);

            if (!StringUtils.hasText(response.token())) {
                throw new RuntimeException("Empty mail.tm token");
            }

            return response.token();

        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve mail.tm token", e);
        }
    }

    // =========================
    // OTP EXTRACTION (BODY)
    // =========================
    @Nullable
    private String extractCode(String messagesJson, OffsetDateTime date, String token) {
        try {
            MailTmMessagesResponse response =
                    OBJECT_MAPPER.readValue(messagesJson, MailTmMessagesResponse.class);

            if (response == null || response.messages() == null) {
                return null;
            }

            for (MailTmMessagesResponse.Message msg : response.messages()) {

                if (msg.createdAt() == null || msg.createdAt().isBefore(date)) {
                    continue;
                }

                Request request = new Request.Builder()
                        .url(BASE_URL + "/messages/" + msg.id())
                        .header("Authorization", "Bearer " + token)
                        .build();

                String fullJson = OK_HTTP_UTIL.handleApiRequest(request);
                MailTmMessageFullResponse full =
                        OBJECT_MAPPER.readValue(fullJson, MailTmMessageFullResponse.class);

                String body = firstNonBlank(
                        full.text(),
                        joinHtml(full.html())
                );

                if (!StringUtils.hasText(body)) {
                    continue;
                }

                var matcher = OTP_PATTERN.matcher(body);
                if (matcher.find()) {
                    String code = matcher.group(1);
                    log.info("OTP retrieved: {}", code);
                    return code;
                }
            }

            return null;

        } catch (Exception e) {
            log.warn("OTP extraction failed", e);
            return null;
        }
    }

    @Nullable
    private String joinHtml(java.util.List<String> html) {
        return (html == null || html.isEmpty()) ? null : String.join("\n", html);
    }
}