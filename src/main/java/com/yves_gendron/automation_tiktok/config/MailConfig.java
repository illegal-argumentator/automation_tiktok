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
        props.put("mail.store.protocol", mailProps.getProtocol());
        props.put("mail.imap.host", mailProps.getHost());
        props.put("mail.imap.port", mailProps.getPort());
        props.put("mail.imap.ssl.enable", mailProps.getSsl());

        Session session = Session.getInstance(props);

        try {
            Store store = session.getStore(mailProps.getProtocol());
            store.connect(mailProps.getUsername(), mailProps.getPassword());
            return store;
        } catch (MessagingException e) {
            log.error("IMAP connection failed: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

}
