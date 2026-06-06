package com.codequest.controller

import com.codequest.dto.LevelProgressDto
import com.codequest.dto.UpdateLevelProgressRequest
import com.codequest.service.LevelProgressService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/levels")
@Tag(name = "Level Progress", description = "Level progress tracking")
@SecurityRequirement(name = "bearerAuth")
class LevelProgressController(
    private val levelProgressService: LevelProgressService
) {

    @GetMapping("/progress")
    @Operation(summary = "Get level progress", description = "Returns progress for a specific level")
    fun getLevelProgress(
        @RequestParam language: String,
        @RequestParam track: String,
        @RequestParam levelId: Int
    ): ResponseEntity<LevelProgressDto?> {
        return ResponseEntity.ok(levelProgressService.getLevelProgress(language, track, levelId))
    }

    @PutMapping("/progress")
    @Operation(summary = "Update level progress", description = "Saves or updates level progress (upsert)")
    fun updateLevelProgress(@RequestBody request: UpdateLevelProgressRequest): ResponseEntity<LevelProgressDto> {
        return ResponseEntity.ok(levelProgressService.updateLevelProgress(request))
    }
}

