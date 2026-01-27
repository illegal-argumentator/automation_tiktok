package com.yves_gendron.automation_tiktok.domain.mail.db.dto;

import java.util.List;

public record MailTmDomainsResponse(List<Domain> domains) {
    public record Domain(String domain) {}
}