package com.yves_gendron.automation_tiktok.domain.mail.db.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record MailTmDomainsResponse(
        @JsonProperty("hydra:member") List<Domain> members
) {
    public record Domain(String domain) {}
}