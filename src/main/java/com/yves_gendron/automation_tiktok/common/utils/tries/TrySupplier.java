package com.yves_gendron.automation_tiktok.common.utils.tries;

public interface TrySupplier<T> {

    T get() throws Exception;
}
