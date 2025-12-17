package com.yves_gendron.automation_tiktok.system.client.mailtm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetTokenResponse {

    private String token;

}
