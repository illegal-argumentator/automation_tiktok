package com.yves_gendron.automation_tiktok.system.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class VideoActionRequest extends ActionRequest {

    @NotNull(message = "Video ID is required")
    private String videoId;

    private LocalDateTime uploadAt;

}
