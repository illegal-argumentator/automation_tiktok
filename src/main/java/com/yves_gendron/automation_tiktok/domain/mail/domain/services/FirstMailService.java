package com.yves_gendron.automation_tiktok.domain.mail.domain.services;

import com.yves_gendron.automation_tiktok.domain.mail.db.entites.MailEntity;
import com.yves_gendron.automation_tiktok.domain.mail.db.repositories.MailRepository;
import com.yves_gendron.automation_tiktok.domain.mail.db.searches.MailSearch;
import com.yves_gendron.automation_tiktok.domain.mail.domain.models.FirstMailApiRespond;
import com.yves_gendron.automation_tiktok.domain.mail.domain.models.GetEmailRequest;
import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class FirstMailService implements MailService{
    private static final String API_KEY = "UbFp9BEzLme_R6xSm0KVP09Ty0V1OrJ6NaHwZXPCoH3MkNOQ5HXUY26FlaCcZyd4";
    private static final String URL = "https://firstmail.ltd/api/v1/";
    private static final RestTemplate  REST_TEMPLATE = new RestTemplate();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(
            "EEE, dd MMM yyyy HH:mm:ss Z"
    );
    private final MailRepository mailRepository;

    @Override
    public String getEmail() {
        var search = MailSearch.builder().isUsed(false).build();
        var entity = mailRepository.findAll(search, Pageable.ofSize(1))
                .stream().findFirst().orElseThrow();
        entity.setUsedAt(OffsetDateTime.now());
        mailRepository.save(entity);
        return entity.getEmail();
    }

    @Override
    public String retrieveCodeFromMessage(String email, OffsetDateTime date) {
        var search = MailSearch.builder().email(email).build();
        var entity = mailRepository.findAll(search, Pageable.ofSize(1))
                .stream().findFirst().orElseThrow();

        var path = URL + "/email/messages";

        var body = GetEmailRequest.builder()
                .email(entity.getEmail())
                .password(entity.getPassword())
                .limit(5)
                .folder("INBOX")
                .build();

        var req = RequestEntity.post(path)
                .header("X-API-KEY", API_KEY)
                .body(body);

        var respond = REST_TEMPLATE.exchange(req, FirstMailApiRespond.class).getBody();
        assert respond != null;
        if (!respond.success()){
            throw new RuntimeException("Failed to retrieve emails");
        }
        return respond.data().messages().stream()
                .filter(it -> it.date().isAfter(date))
                .map(this::getCode)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow();
    }

    @Nullable
    private String getCode(FirstMailApiRespond.Message message) {
        String subject = message.subject();
        if (!StringUtils.hasText(subject)) {
            return null;
        }
        if (subject.matches("^\\d{6}\\s.*")) {
            String code = subject.substring(0, 6);
            log.info("Successfully retrieved code from message: {}", code);
            return code;
        }
        return null;
    }


    @PostConstruct
    private void init() {
        System.out.println(retrieveCodeFromMessage("ftmxfcdm@duhastmail.com", OffsetDateTime.now().minusDays(1)));
    }

    private void importEmails() throws IOException {
        var emails = mailRepository.getEmails();

        var path = Path.of("text.txt");
        var content = Files.readString(path);
        var lines = content.split("\n");
        var entities = new ArrayList<MailEntity>();
        for (String line : lines) {
            var parts = line.split(":");
            var email = parts[0].trim();
            var password = parts[1].trim();
            if (emails.contains(email)) {
                continue;
            }
            var entity = MailEntity.builder()
                    .email(email)
                    .password(password)
                    .provider("FIRSTMAIL")
                    .createdAt(OffsetDateTime.now())
                    .build();
            entities.add(entity);
        }
        if (!entities.isEmpty()) {
            mailRepository.saveAll(entities);
        }

    }

}
