package com.yves_gendron.automation_tiktok.domain.mail.db.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record MailTmMessagesResponse(List<Message> messages) {
    public record Message(String subject, OffsetDateTime createdAt) {}
}