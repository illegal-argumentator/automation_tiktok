package com.yves_gendron.automation_tiktok.system.client.sadcaptcha.common.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RotateCaptchaRequest {

    private final String outerImageB64;

    private final String innerImageB64;

}
