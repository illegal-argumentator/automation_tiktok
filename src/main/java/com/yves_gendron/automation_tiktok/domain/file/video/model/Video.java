package com.yves_gendron.automation_tiktok.domain.file.video.model;

import com.yves_gendron.automation_tiktok.common.dto.AuditingEntity;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Blob;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Video extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;

    private String contentType;

    @Lob
    private Blob dataBlob;
}
