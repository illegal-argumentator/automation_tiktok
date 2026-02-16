package com.yves_gendron.automation_tiktok.system.controller.dto;

import com.yves_gendron.automation_tiktok.common.type.Platform;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAccountsRequest {

    @NotNull(message = "Platform is required")
    private Platform platform;

    @Min(value = 1, message = "Required minimum 1 account to create")
    @Max(value = 10, message = "Maximum 10 accounts to create")
    private int amount;

}
