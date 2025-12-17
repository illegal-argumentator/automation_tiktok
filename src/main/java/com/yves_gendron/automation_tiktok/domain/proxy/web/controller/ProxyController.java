package com.yves_gendron.automation_tiktok.domain.proxy.web.controller;

import com.yves_gendron.automation_tiktok.domain.proxy.model.Proxy;
import com.yves_gendron.automation_tiktok.domain.proxy.service.ProxyService;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.AddProxyRequest;
import com.yves_gendron.automation_tiktok.domain.proxy.web.dto.UpdateProxyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/proxies")
public class ProxyController {

    private final ProxyService proxyService;

    @PostMapping("/all")
    public void addProxies(@Valid @RequestBody AddProxyRequest addProxyRequest) {
        proxyService.saveAll(addProxyRequest);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Proxy>> findAll() {
        return ResponseEntity.ok(proxyService.findAll());
    }

    @PutMapping("/{id}")
    public void update(@PathVariable String id, @RequestBody UpdateProxyRequest updateProxyRequest) {
        proxyService.update(id, updateProxyRequest);
    }
}
