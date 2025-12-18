package com.yves_gendron.automation_tiktok.domain.proxy.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateProxyRequest {

    private Boolean verified;

    private String username;

    private String password;

    private String host;

    private String countryCode;

    private String timezone;

    private Integer port;

    private Integer accountsLinked;
}
