package com.yves_gendron.automation_tiktok.system.client.ip_api.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetProxyAddressResponse {

   private String country;

   private String countryCode;

   private String regionName;

    private int lat;

    private int lon;

   private String timezone;
}
