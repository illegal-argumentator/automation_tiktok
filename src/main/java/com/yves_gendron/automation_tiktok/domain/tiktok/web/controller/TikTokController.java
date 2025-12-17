package com.yves_gendron.automation_tiktok.domain.tiktok.web.controller;

import com.yves_gendron.automation_tiktok.domain.tiktok.model.TikTokAccount;
import com.yves_gendron.automation_tiktok.domain.tiktok.service.TikTokService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tiktok")
@RequiredArgsConstructor
public class TikTokController {

    private final TikTokService tikTokService;

    @GetMapping("/all")
    public ResponseEntity<List<TikTokAccount>> findAll() {
        return ResponseEntity.ok(tikTokService.findAll());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        tikTokService.delete(id);
    }
}