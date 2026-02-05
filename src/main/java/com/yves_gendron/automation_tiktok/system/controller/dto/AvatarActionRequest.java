package com.yves_gendron.automation_tiktok.system.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AvatarActionRequest extends ActionRequest {

    @NotBlank(message = "Photo is required")
    private String photoId;

}
