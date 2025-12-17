package com.yves_gendron.automation_tiktok.system.controller.dto;

import com.yves_gendron.automation_tiktok.common.type.Platform;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActionRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

}
