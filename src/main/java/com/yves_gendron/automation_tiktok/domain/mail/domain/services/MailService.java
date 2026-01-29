package com.yves_gendron.automation_tiktok.domain.mail.domain.services;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface MailService {

    String getEmail();

    String retrieveCodeFromMessage(String email, OffsetDateTime date) throws InterruptedException;
}
