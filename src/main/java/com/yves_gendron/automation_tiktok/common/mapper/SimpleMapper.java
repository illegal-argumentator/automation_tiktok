package com.yves_gendron.automation_tiktok.common.mapper;

public interface SimpleMapper<T, E> {

    E mapDtoToEntity(T dto);

}
