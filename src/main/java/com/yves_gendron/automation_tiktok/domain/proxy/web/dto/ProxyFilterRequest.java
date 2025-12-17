package com.yves_gendron.automation_tiktok.domain.proxy.web.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProxyFilterRequest {

    private String username;

    private Boolean verified;

    private Integer accountsLinkedLessThan;

}
