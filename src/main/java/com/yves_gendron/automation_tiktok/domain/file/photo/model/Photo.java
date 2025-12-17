package com.yves_gendron.automation_tiktok.domain.file.photo.model;

import com.yves_gendron.automation_tiktok.common.dto.AuditingEntity;
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Photo extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String contentType;

    @Lob
    private byte[] data;
}
