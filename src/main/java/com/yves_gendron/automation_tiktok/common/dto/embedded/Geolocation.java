package com.yves_gendron.automation_tiktok.common.dto.embedded;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Geolocation {

    private String countryCode;

    private String regionName;

}
