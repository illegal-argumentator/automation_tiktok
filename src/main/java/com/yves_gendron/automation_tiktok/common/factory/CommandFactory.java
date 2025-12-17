package com.yves_gendron.automation_tiktok.common.factory;

import com.yves_gendron.automation_tiktok.common.type.Platform;

public interface CommandFactory<T> {

    T getCommandByAction(Platform platform);

}
