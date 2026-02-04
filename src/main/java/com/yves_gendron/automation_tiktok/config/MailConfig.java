package com.yves_gendron.automation_tiktok.config;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProps mailProps;

    @Bean
    public Store getImapStore() {
        Properties props = new Properties();

        props.put("mail.store.protocol", "imap");

        props.put("mail.imap.host", "mail.privateemail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.auth", "true");

        props.put("mail.imap.starttls.enable", "false");
        props.put("mail.imap.ssl.trust", "mail.privateemail.com");

        Session session = Session.getInstance(props);

        try {
            Store store = session.getStore("imap");

            store.connect(
                    "mail.privateemail.com",
                    "mormul.mail@formormul.xyz",
                    mailProps.getPassword()
            );

            return store;
        } catch (MessagingException e) {
            log.error("IMAP connection failed", e);
            throw new RuntimeException(e);
        }
    }
}
