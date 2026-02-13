package com.yves_gendron.automation_tiktok.system.client.randomuser.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NinjaRandomResponse(List<NinjaUser> data) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NinjaUser(
            String firstName,
            String lastName,
            String email,
            String dob,
            int age,
            String picture
    ) { }
}
