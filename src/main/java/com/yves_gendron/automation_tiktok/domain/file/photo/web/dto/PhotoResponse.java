package com.yves_gendron.automation_tiktok.domain.file.photo.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PhotoResponse {

    private String id;

    private String title;

    private String photoLink;

    private LocalDateTime createdDate;

    private LocalDateTime lastModifiedDate;

}
