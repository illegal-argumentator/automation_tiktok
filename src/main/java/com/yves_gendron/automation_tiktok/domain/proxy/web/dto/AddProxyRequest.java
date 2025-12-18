package com.yves_gendron.automation_tiktok.domain.proxy.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AddProxyRequest {

    @Valid
    @NotNull(message = "Proxies is required")
    private List<ProxyRequest> proxies;

    @Data
    public static class ProxyRequest {

        @NotBlank(message = "Username is required")
        private String username;

        @NotBlank(message = "Password is required")
        private String password;

        @NotBlank(message = "Host is required")
        private String host;

        @NotNull(message = "Port is required")
        private Integer port;

        @NotNull(message = "Rotation link is required")
        private String autoRotationLink;
    }
}
