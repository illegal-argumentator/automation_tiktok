package com.yves_gendron.automation_tiktok.domain.mail.utils;


import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class OffsetDateTimeDeserializer extends ValueDeserializer<OffsetDateTime> {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z");
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt)  {
        String raw = p.getText();

        // Удаляем часть в скобках, например " (UTC)"
        String cleaned = raw.replaceAll("\\s*\\(.*?\\)$", "");

        return OffsetDateTime.parse(cleaned, FORMATTER);
    }


}
