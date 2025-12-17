package com.yves_gendron.automation_tiktok.system.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class AvatarActionRequest extends ActionRequest {

    @NotBlank(message = "Photo is required")
    private String photoId;

}
