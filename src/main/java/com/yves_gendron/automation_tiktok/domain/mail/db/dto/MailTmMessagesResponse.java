package com.yves_gendron.automation_tiktok.domain.mail.db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record MailTmMessagesResponse(
        @JsonProperty("hydra:member")
        List<Message> messages
) {
    public record Message(
            String id,
            String subject,
            String intro,
            OffsetDateTime createdAt
    ) {}
}