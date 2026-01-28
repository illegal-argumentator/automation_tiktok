package com.yves_gendron.automation_tiktok.domain.mail.db.dto;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;
import java.util.List;

public record MailTmMessageFullResponse(
        String id,
        String subject,
        String text,
        List<String> html,
        OffsetDateTime createdAt
) {}