package com.yves_gendron.automation_tiktok.domain.file.video.web.dto;

import com.yves_gendron.automation_tiktok.common.dto.AuditingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class VideoResponse extends AuditingEntity {

    private String id;

    private String title;

    private String videoLink;

}