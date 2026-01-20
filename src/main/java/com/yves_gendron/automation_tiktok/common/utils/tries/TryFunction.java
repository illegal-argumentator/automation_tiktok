package com.yves_gendron.automation_tiktok.common.utils.tries;

public interface TryFunction<T,R> {

    R apply(T t) throws Exception;
}
