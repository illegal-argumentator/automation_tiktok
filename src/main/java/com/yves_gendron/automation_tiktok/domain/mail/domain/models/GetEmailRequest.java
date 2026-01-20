package com.yves_gendron.automation_tiktok.domain.mail.domain.models;

import lombok.Builder;

@Builder
public record GetEmailRequest(
        String email,
        String password,
        int limit,
        String folder
) {
}
