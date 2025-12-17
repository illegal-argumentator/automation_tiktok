package com.yves_gendron.automation_tiktok.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Action {

    LIKE(true),
    COMMENT(true),
    VIDEO(true),
    AVATAR(true),
    FAILED(false),
    ACTED(false);

    private final boolean actable;
}
