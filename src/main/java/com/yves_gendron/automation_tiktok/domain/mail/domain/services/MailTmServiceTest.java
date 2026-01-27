package com.yves_gendron.automation_tiktok.domain.mail.domain.services;

import com.yves_gendron.automation_tiktok.domain.mail.db.entites.MailEntity;
import com.yves_gendron.automation_tiktok.domain.mail.db.repositories.MailRepository;
import com.yves_gendron.automation_tiktok.domain.mail.db.searches.MailSearch;
import com.yves_gendron.automation_tiktok.domain.mail.db.dto.*;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.OffsetDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
class MailTmServiceTest implements MailService {

    private static final String BASE_URL = "https://api.mail.tm";
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

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

        mailRepository.save(entity);
        return email;
    }

    @Override
    public String retrieveCodeFromMessage(String email, OffsetDateTime date) {
        var search = MailSearch.builder().email(email).build();
        var entity = mailRepository.findAll(search, Pageable.ofSize(1))
                .stream()
                .findFirst()
                .orElseThrow();

        var req = RequestEntity.get(BASE_URL + "/messages")
                .header("Authorization", "Bearer " + entity.getAccessToken())
                .build();

        var response = REST_TEMPLATE
                .exchange(req, MailTmMessagesResponse.class)
                .getBody();

        if (response == null || response.messages() == null) {
            throw new RuntimeException("No messages from mail.tm");
        }

        return response.messages().stream()
                .filter(m -> m.createdAt().isAfter(date))
                .map(this::extractCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
    }

    private String fetchDomain() {
        var response = REST_TEMPLATE.getForObject(
                BASE_URL + "/domains",
                MailTmDomainsResponse.class
        );

        if (response == null || response.domains().isEmpty()) {
            throw new RuntimeException("No available mail.tm domains");
        }

        return response.domains().get(0).domain();
    }

    private void createAccount(String email, String password) {
        REST_TEMPLATE.postForEntity(
                BASE_URL + "/accounts",
                new MailTmAccountRequest(email, password),
                Void.class
        );
    }

    private String getToken(String email, String password) {
        var response = REST_TEMPLATE.postForObject(
                BASE_URL + "/token",
                new MailTmTokenRequest(email, password),
                MailTmTokenResponse.class
        );

        if (response == null || !StringUtils.hasText(response.token())) {
            throw new RuntimeException("Failed to retrieve mail.tm token");
        }

        return response.token();
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
        return null;
    }
}