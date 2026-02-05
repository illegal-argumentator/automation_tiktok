package com.yves_gendron.automation_tiktok.system.controller.dto;

import com.yves_gendron.automation_tiktok.common.type.Platform;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ActionRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

}
