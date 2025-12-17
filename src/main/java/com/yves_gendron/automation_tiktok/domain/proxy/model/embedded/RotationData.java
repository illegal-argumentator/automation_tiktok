package com.yves_gendron.automation_tiktok.domain.proxy.model.embedded;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class RotationData {

    private Long autoRotateInterval;

    private Instant lastRotation;

    private String autoRotationLink;

}
