package com.yves_gendron.automation_tiktok.domain.proxy.common.mapper;

import com.yves_gendron.automation_tiktok.common.mapper.SimpleMapper;
import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.model.embedded.RotationData;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.AddProxyRequest;
import org.springframework.stereotype.Component;

@Component
public class ProxyMapper implements SimpleMapper<AddProxyRequest.ProxyRequest, Proxy> {

    @Override
    public Proxy mapDtoToEntity(AddProxyRequest.ProxyRequest dto) {
        return Proxy.builder()
                .username(dto.getUsername())
                .password(dto.getPassword())
                .host(dto.getHost())
                .port(dto.getPort())
                .rotationData(RotationData.builder()
                        .autoRotateInterval(dto.getAutoRotateInterval())
                        .autoRotationLink(dto.getAutoRotationLink())
                        .build())
                .build();
    }
}
