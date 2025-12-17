package com.yves_gendron.automation_tiktok.common.dto.embedded;

import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Dob;
import com.yves_gendron.automation_tiktok.domain.tiktok.model.embedded.Name;
import jakarta.persistence.Embedded;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bio {

    @Embedded
    private Name name;

    @Embedded
    private Dob dob;

    @Embedded
    private Geolocation geolocation;

}
