package com.yves_gendron.automation_tiktok.system.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class VideoActionRequest extends ActionRequest {

    @NotNull(message = "Video ID is required")
    private String videoId;

    private LocalDateTime uploadAt;

}
