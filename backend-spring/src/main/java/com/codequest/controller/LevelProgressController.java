package com.codequest.controller;

import com.codequest.dto.LevelProgressDto;
import com.codequest.dto.UpdateLevelProgressRequest;
import com.codequest.service.LevelProgressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/levels")
@Tag(name = "Level Progress")
@SecurityRequirement(name = "bearerAuth")
public class LevelProgressController {

    private final LevelProgressService levelProgressService;

    public LevelProgressController(LevelProgressService levelProgressService) {
        this.levelProgressService = levelProgressService;
    }

    @GetMapping("/progress")
    public ResponseEntity<?> getLevelProgress(
            @RequestParam String language,
            @RequestParam String track,
            @RequestParam Integer levelId) {
        Optional<LevelProgressDto> progress = levelProgressService.getLevelProgress(language, track, levelId);
        return ResponseEntity.ok(progress.orElse(null));
    }

    @GetMapping("/progress/all")
    @Operation(summary = "Get all progress for current user")
    public ResponseEntity<List<LevelProgressDto>> getAllProgress() {
        return ResponseEntity.ok(levelProgressService.getAllProgress());
    }

    @PutMapping("/progress")
    public ResponseEntity<LevelProgressDto> updateLevelProgress(@RequestBody UpdateLevelProgressRequest request) {
        return ResponseEntity.ok(levelProgressService.updateLevelProgress(request));
    }
}
