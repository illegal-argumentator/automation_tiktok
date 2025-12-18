package com.yves_gendron.automation_tiktok.domain.proxy.model;

import com.yves_gendron.automation_tiktok.common.dto.AuditingEntity;
import com.yves_gendron.automation_tiktok.domain.proxy.model.embedded.Geolocation;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Proxy extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;

    private String password;

    private String host;

    private int port;

    @Embedded
    private Geolocation geolocation;

    private boolean verified;

    private int accountsLinked;

    private String autoRotationLink;
}