package com.yves_gendron.automation_tiktok.system.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentActionRequest extends ActionRequest {

    @Min(value = 1, message = "Minimum actions is 1")
    @Max(value = 10, message = "Maximum actions is 10")
    private Integer actions;

}
