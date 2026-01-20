package com.yves_gendron.automation_tiktok.domain.mail.domain.models;

import com.yves_gendron.automation_tiktok.domain.mail.utils.OffsetDateTimeDeserializer;
import tools.jackson.databind.annotation.JsonDeserialize;

import java.time.OffsetDateTime;
import java.util.List;

public record FirstMailApiRespond(
        boolean success,
        Data data
) {

    public record Data(
            List<Message> messages,
            int total_count,
            int unread_count
    ) {}

    public record Message(
            String subject,
            String from,
            @JsonDeserialize(using = OffsetDateTimeDeserializer.class)
            OffsetDateTime date,
            String body_text,
            String body_html,
            boolean is_read,
            boolean is_flagged,
            String id,
            int uid
    ) {}
}
